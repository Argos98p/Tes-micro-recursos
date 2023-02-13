package com.turisup.resources.service;

import com.complexible.stardog.api.Connection;
import com.complexible.stardog.jena.SDJenaFactory;
import com.turisup.resources.model.parser.Parser;
import com.turisup.resources.model.request.post.AddComment;
import com.turisup.resources.repository.DBConnection;
import com.turisup.resources.repository.FBConnection;
import com.turisup.resources.repository.SparqlTemplates;
import com.turisup.resources.repository.StardogHttpQueryConn;
import org.apache.jena.geosparql.implementation.WKTLiteralFactory;
import org.apache.jena.geosparql.implementation.vocabulary.Geo;
import org.apache.jena.query.*;
import org.apache.jena.rdf.model.*;
import org.apache.jena.sparql.vocabulary.FOAF;
import org.apache.jena.vocabulary.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;

import static com.turisup.resources.repository.SparqlTemplates.deleteComent;

@Service
public class CommentService {
    @Autowired
    FileStorageService fileStorageService;
    StardogHttpQueryConn stardogHttpQueryConn;
    final String vcard="http://www.w3.org/2006/vcard/ns";
    final String geo="http://www.opengis.net/ont/geosparql#";
    final String BASE="http://turis-ucuenca";
    final String tp ="http://tour-pedia.org/download/tp.owl";
    public HashMap<String, Object> save(AddComment newComment, MultipartFile[] files) {
        try (Connection myConnection = DBConnection.createConnection()){
            Model myModel = SDJenaFactory.createModel(myConnection);
            myModel.begin();
            if(files == null){
                files = new MultipartFile[]{};
            }
            ArrayList<String> imagesPaths= new ArrayList<>();
            for (MultipartFile file : files) {
                imagesPaths.add(fileStorageService.storeFile(file, newComment.getLugarId()));
            }
            ArrayList<ArrayList<String>> srcAndIds = FBConnection.ToFacebook(imagesPaths);
            ArrayList <String> srcImages = srcAndIds.get(0);
            ArrayList <String> fbImagesIds = srcAndIds.get(1);
            Bag facebookIds = myModel.createBag();

            Resource userId = myModel.getResource("http://turis-ucuenca/user/"+newComment.getUserId());
            Resource placeResource = myModel.getResource("http://turis-ucuenca/lugar/"+newComment.getLugarId());



            if(userId .getProperty(FOAF.name) == null){
                return new HashMap<>(){{
                    put("error","Usuario no encontrado");
                }};
            }
            if(placeResource.getProperty(DC.title)== null){
                return new HashMap<>(){{
                    put("error","Lugar no encontrado");
                }};
            }

            String comentarioId = UUID.randomUUID().toString();
            Resource commentResource = myModel.createResource("http://turis-ucuenca/comentario/"+comentarioId );
            commentResource.addProperty(RDF.type, myModel.createResource("http://turis-ucuenca/Comentario"));
            commentResource.addProperty(myModel.createProperty(BASE,"#place"),placeResource);
            commentResource.addLiteral(myModel.createProperty(BASE,"#text"), newComment.getComentario());
            commentResource.addLiteral(myModel.createProperty(BASE,"#rating"),newComment.getPuntaje());
            if(imagesPaths.size()>0){
                commentResource.addProperty(myModel.createProperty(BASE,"#facebookId"),facebookIds);
                for (int i=0; i<srcAndIds.get(0).size();i++){
                    commentResource.addProperty(VCARD4.hasPhoto,(srcImages.get(i)));
                    facebookIds.add(fbImagesIds.get(i));
                }
            }
            userId.addProperty(myModel.createProperty(BASE,"#comment"),commentResource);

            userId.getProperty(FOAF.name);
            userId.getProperty(FOAF.depiction);
            myModel.commit();


            return new HashMap<String, Object>() {{
                put("id",comentarioId );
                put("comentario", newComment.getComentario());
                put("puntaje",newComment.getPuntaje());
                put("imagenes",srcImages);
                put("user", new HashMap<String,String>(){{
                    put("nombre",userId.getProperty(FOAF.name).getObject().toString());
                    put("imagen",userId.getProperty(FOAF.depiction).getObject().toString());
                }});

            }};
        }finally {
        }
    }

    public ArrayList<Map<String, Object>> getByPlaces(String lugarId) {
            ArrayList<Map<String,Object>> comentarios=new ArrayList<>();
        try (Connection myConnection = DBConnection.createConnection()) {
            Model myModel = SDJenaFactory.createModel(myConnection);
            Resource lugarResource = myModel.getResource("http://turis-ucuenca/lugar/"+lugarId );
            if(lugarResource.getProperty(DC.title)==null){
                comentarios.add(new HashMap<>() {{put("error","No se encontro el lugar");}});
                return comentarios;
            }
            String queryString = SparqlTemplates.getComentsInPlace(lugarId);
            Query query = QueryFactory.create(queryString);
            QueryExecution qexec = QueryExecutionFactory.create(query,myModel);
            try {
                ResultSet results= qexec.execSelect();
                while(results.hasNext()){
                    QuerySolution soln = results.nextSolution();
                    Map<String, Object> comentario = new HashMap<>();
                    Resource userId = myModel.getResource(soln.getResource("user").toString());
                    Resource coment = myModel.getResource(soln.getResource("com").toString());
                    comentario.put("user",new HashMap<>(){{put("id",userId.toString().replace("http://turis-ucuenca/user/",""));
                    put("nombre",soln.getLiteral("nombreUser").toString());
                    put("foto",soln.getResource("fotoUser").toString());}});
                    comentario.put("imagenes",new ArrayList( Arrays.asList( soln.getLiteral("imagenes").toString().split(",") ) ));
                    comentario.put("comentario",soln.getLiteral("comentario").toString());
                    comentario.put("puntaje", soln.getLiteral("puntaje").getInt());
                    comentario.put("id",coment.toString().replace("http://turis-ucuenca/comentario/",""));
                    comentarios.add(comentario);

                }
            }finally {
                qexec.close();
            }

        }finally {

        }
        return comentarios;
    }

    public boolean delteComent(String comentarioId) {
        try (Connection myConnection = DBConnection.createConnection()) {
            Model myModel = SDJenaFactory.createModel(myConnection);
            Resource comentario = myModel.getResource(BASE + "/comentario/" + comentarioId);
            if (comentario.getProperty(myModel.createProperty(BASE, "#text")) == null) {
                return false;
            }
            String queryDelete = SparqlTemplates.deleteComent(comentarioId);
            stardogHttpQueryConn = new StardogHttpQueryConn();
            stardogHttpQueryConn.PostToTriplestore(queryDelete);
            /*
            myModel.begin();
            myModel.removeAll((Resource) null, myModel.createProperty(BASE, "#comment"),comentario);

            myModel.commit();*/


        }
        return true;
    }
}
