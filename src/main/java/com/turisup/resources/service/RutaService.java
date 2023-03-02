package com.turisup.resources.service;

import com.complexible.stardog.api.Connection;
import com.complexible.stardog.jena.SDJenaFactory;
import com.turisup.resources.model.parser.Parser;
import com.turisup.resources.repository.DBConnection;
import com.turisup.resources.repository.FBConnection;
import com.turisup.resources.repository.SparqlTemplates;
import com.turisup.resources.repository.StardogHttpQueryConn;
import com.turisup.resources.utils.Utils;
import org.apache.jena.geosparql.implementation.WKTLiteralFactory;
import org.apache.jena.geosparql.implementation.vocabulary.Geo;
import org.apache.jena.query.*;
import org.apache.jena.rdf.model.*;
import org.apache.jena.vocabulary.*;
import org.springframework.stereotype.Service;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
@Service
public class RutaService {

    final String vcard="http://www.w3.org/2006/vcard/ns";
    final String geo="http://www.opengis.net/ont/geosparql#";
    StardogHttpQueryConn stardogHttpQueryConn;
    final String BASE="http://turis-ucuenca";
    final String tp ="http://tour-pedia.org/download/tp.owl";
    public  ArrayList<Map<String, String>> getRutasUser(String userId) {
        ArrayList<Map<String,String>> rutas = new ArrayList<>();
        try (Connection myConnection = DBConnection.createConnection()){
            Model myModel = SDJenaFactory.createModel(myConnection);
            String queryString = SparqlTemplates.rutasByUser(userId);
            Query query = QueryFactory.create(queryString);
            QueryExecution qexec = QueryExecutionFactory.create(query,myModel);
            try {
                ResultSet results= qexec.execSelect();
                while(results.hasNext()){
                    QuerySolution soln = results.nextSolution();
                    Map<String, String> ruta = new HashMap<>();
                    ruta.put("rutaId",soln.getResource("ruta").toString().replace("http://turis-ucuenca/ruta/",""));
                    ruta.put("nombre",soln.getLiteral("nombre").toString());
                    rutas.add(ruta);

                }
            }finally {
                qexec.close();
            }
        }finally {
        }
        return rutas;
    }

    public Map<String, Object> getOneRuta(String rutaId) {
        Map<String, Object> ruta = new HashMap<>();
        try (Connection myConnection = DBConnection.createConnection()){
            Model myModel = SDJenaFactory.createModel(myConnection);
            String queryString = SparqlTemplates.getRuta(rutaId);
            Query query = QueryFactory.create(queryString);
            QueryExecution qexec = QueryExecutionFactory.create(query,myModel);
            String node_id="";
            try {
                ResultSet results= qexec.execSelect();
                ArrayList<Map<String, Object>> placesInRoute = new ArrayList<>();
                while(results.hasNext()){
                    System.out.println("entra una vez");
                    QuerySolution soln = results.nextSolution();
                    ruta.put("id",rutaId);
                    ruta.put("nombre",soln.getLiteral("nombre").toString());
                    ruta.put("descripcion",soln.getLiteral("descripcion").toString());
                    ruta.put("creador",soln.getResource("creador").toString().replace("http://turis-ucuenca/user/",""));
                    node_id=soln.getResource("placeNode").toString();
                }
                if(!node_id.equals("")){
                    System.out.println(node_id);
                    String queryRutaGetPlaces = SparqlTemplates.getRutaPlaces(node_id);
                    Query query2 = QueryFactory.create(queryRutaGetPlaces);
                    QueryExecution qexec2 = QueryExecutionFactory.create(query2,myModel);
                    ResultSet results2= qexec2.execSelect();
                    while(results2.hasNext()){
                        System.out.println("entra 3");
                        QuerySolution soln2 = results2.nextSolution();

                        if(soln2.getResource("placeId")==null){

                        }else{
                            Map<String,Object> place = new HashMap<>();
                            String placeId = soln2.getResource("placeId").toString().replace("http://turis-ucuenca/lugar/","");
                            String titulo = soln2.getLiteral("nombre").toString();
                            String descripcion = soln2.getLiteral("descripcion").toString();
                            Point2D.Double point= Utils.literalToPoint(soln2.getLiteral("point"));
                            String creadoPor= soln2.getResource("creador").toString().replace("http://turis-ucuenca/user/","");
                            ArrayList<String> facebookImagesUrls= new ArrayList( Arrays.asList( soln2.getLiteral("imagenes").toString().split(",") ) );
                            place.put("id",placeId);
                            place.put("nombre",titulo);
                            place.put("descripcion",descripcion);
                            place.put("coordenadas",new HashMap<String, Double>() {{
                                put("latitud", point.x);
                                put("longitud", point.y);
                            }});
                            place.put("creadorId",creadoPor);
                            place.put("imagenes",facebookImagesUrls);
                            placesInRoute.add(place);
                        }

                    }
                    ruta.put("lugares",placesInRoute);
                }
            }finally {
                qexec.close();
            }


        }finally {
        }
        return ruta;
    }

    public Map<String, Object> agregarLugar(String rutaId, ArrayList<String> lugarId) {

        try (Connection myConnection = DBConnection.createConnection()) {
            Model myModel = SDJenaFactory.createModel(myConnection);
            myModel.begin();
            Resource myRoute = myModel.getResource("http://turis-ucuenca/ruta/"+rutaId);
            Statement st = myRoute.getProperty(myModel.createProperty(BASE,"/hasPlaces"));
            Seq placesSequ = st.getSeq();
            for(int i=0;i<lugarId.size();i++){
                placesSequ.add(myModel.getResource("http://turis-ucuenca/lugar/"+lugarId.get(i)));
            }


            myModel.commit();
        }
        return getOneRuta(rutaId);
    }

    public Map<String, Object> eliminarLugar(String rutaId, String lugarId) {

        try (Connection myConnection = DBConnection.createConnection()) {
            Model myModel = SDJenaFactory.createModel(myConnection);
            String queryString = SparqlTemplates.eliminarLugarEnRuta(rutaId,lugarId);
            stardogHttpQueryConn = new StardogHttpQueryConn();
            stardogHttpQueryConn.PostToTriplestore(queryString);
        }
        return getOneRuta(rutaId);

    }


    public ArrayList<Map<String, String>> removeRuta(String userId, String rutaId) {
        String queryString = SparqlTemplates.eliminarRuta(rutaId);
        stardogHttpQueryConn = new StardogHttpQueryConn();
        stardogHttpQueryConn.PostToTriplestore(queryString);
        return getRutasUser(userId);
    }
}
