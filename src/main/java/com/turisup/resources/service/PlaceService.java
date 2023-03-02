package com.turisup.resources.service;

import com.complexible.stardog.api.Connection;
import com.complexible.stardog.jena.SDJenaFactory;
import com.turisup.resources.model.PlacePoint;
import com.turisup.resources.model.PlaceResponse;
import com.turisup.resources.model.parser.Parser;
import com.turisup.resources.model.parser.QueryOptions;
import com.turisup.resources.model.request.post.AddRoute;
import com.turisup.resources.repository.FBConnection;
import com.turisup.resources.repository.SparqlTemplates;
import org.apache.jena.geosparql.implementation.WKTLiteralFactory;
import org.apache.jena.geosparql.implementation.vocabulary.Geo;
import org.apache.jena.query.*;
import org.apache.jena.rdf.model.*;
import org.apache.jena.vocabulary.*;
import com.turisup.resources.model.Place;
import com.turisup.resources.model.request.post.PlaceRequest;
import com.turisup.resources.repository.DBConnection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Service
public class PlaceService {

     final String vcard="http://www.w3.org/2006/vcard/ns";
     final String geo="http://www.opengis.net/ont/geosparql#";
     final String BASE="http://turis-ucuenca";
     final String tp ="http://tour-pedia.org/download/tp.owl";

     @Autowired
     FileStorageService fileStorageService;
     public Place save(PlaceRequest pr, MultipartFile[] files){
          Place newPlace = new Place(UUID.randomUUID().toString(),pr.getNombre(),new PlacePoint(pr.getLatitud(),pr.getLongitud()), pr.getDescripcion(), pr.getUsuarioId());
          ArrayList<String> imagesPaths= new ArrayList<>();
          for (MultipartFile file : files) {
               imagesPaths.add(fileStorageService.storeFile(file, newPlace.getId()));
          }
          try (Connection myConnection = DBConnection.createConnection()){

               Model myModel = SDJenaFactory.createModel(myConnection);
               myModel.begin();
               System.out.println(newPlace.getId());
               Bag facebookIds = myModel.createBag();
               Resource placeModel = myModel.createResource("http://turis-ucuenca/lugar/"+newPlace.getId());
               placeModel.addProperty(RDF.type,myModel.createProperty(tp,"POI"));
               placeModel.addProperty(RDF.type, OWL2.NamedIndividual);
               placeModel.addProperty(DC.title, newPlace.getNombre());
               placeModel.addProperty(RDFS.label,newPlace.getNombre());
               placeModel.addProperty(DC.description,newPlace.getDescripcion());
               placeModel.addProperty(DC.date,  myModel.createTypedLiteral(ZonedDateTime.now( ZoneOffset.UTC ).format( DateTimeFormatter.ISO_INSTANT ), "http://www.w3.org/2001/XMLSchema#dateTime"));
               placeModel.addProperty(myModel.createProperty(BASE,"#status"),"revisar");
               placeModel.addProperty(myModel.createProperty(BASE,"category"),"categoria");
               //La siguiente propiedad indica que el recurso no es valido, es para borrar despues de la ontologia
               placeModel.addProperty(myModel.createProperty(BASE,"#isValid"),myModel.createTypedLiteral(Boolean.FALSE));
               placeModel.addProperty(myModel.createProperty(BASE,"#facebookId"),facebookIds);
               placeModel.addProperty(DC.creator,myModel.createResource(BASE+"/user/"+newPlace.getUsuarioId()));
               ArrayList<ArrayList<String>> srcAndIds = FBConnection.ToFacebook(imagesPaths);
               newPlace.setImagenesPaths(srcAndIds.get(0));
               newPlace.setFbImagenesIds(srcAndIds.get(1));

               for (int i=0; i<srcAndIds.get(0).size();i++){
                    placeModel.addProperty(VCARD4.hasPhoto,(newPlace.getImagenesPaths().get(i)));
                    facebookIds.add(newPlace.getFbImagenesIds().get(i));
               }

               placeModel.addProperty(Geo.HAS_GEOMETRY_PROP,myModel.createResource()
                       .addProperty(RDF.type, myModel.createResource(geo+"Geometry"))
                       .addProperty(Geo.AS_WKT_PROP, WKTLiteralFactory.createPoint(newPlace.getCoordenadas().getLongitud(),newPlace.getCoordenadas().getLatitud())));
               myModel.commit();
          }finally {
          }
          return newPlace;
     }
     public static ArrayList<String> getImageUrls(ArrayList<String> placeIds){
          return FBConnection.getImagesSrcById(placeIds);
     }

     public PlaceResponse get(String placeId){
          PlaceResponse myNewPlace=new PlaceResponse();
          try(Connection myConnection = DBConnection.createConnection()){
               Model myModel = SDJenaFactory.createModel(myConnection);
               String queryString = SparqlTemplates.getPlace(placeId);
               Query query = QueryFactory.create(queryString);
               QueryExecution qexec = QueryExecutionFactory.create(query,myModel);
               try {
                    ResultSet results= qexec.execSelect();
                    while(results.hasNext()){
                         QuerySolution soln = results.nextSolution();
                         myNewPlace= Parser.QueryResult2Place(soln);
                         myNewPlace.setId(placeId);
                    }
               }finally {
                    qexec.close();
               }
          }
          return myNewPlace;
     }

     public List<PlaceResponse> all ( QueryOptions queryOptions ){

          List<PlaceResponse> places = new ArrayList<>();
          try(Connection myConnection = DBConnection.createConnection()){
               Model myModel = SDJenaFactory.createModel(myConnection);
               String queryString = SparqlTemplates.defaultQuery(queryOptions);

               Query query = QueryFactory.create(queryString);
               QueryExecution qexec = QueryExecutionFactory.create(query,myModel);
               try {
                    ResultSet results= qexec.execSelect();
                    while(results.hasNext()){
                         QuerySolution soln = results.nextSolution();
                         PlaceResponse placeResponse = Parser.QueryResult2Place(soln);
                         if(placeResponse!=null){
                              places.add(placeResponse);
                         }
                    }
               }finally {
                    qexec.close();
               }
          }
          return places;
     }

    public String addRoute(AddRoute nuevaRutaInfo) {
         try (Connection myConnection = DBConnection.createConnection()){
              Model myModel = SDJenaFactory.createModel(myConnection);
              myModel.begin();
              String rutaId=UUID.randomUUID().toString();
              Resource rutaResource = myModel.createResource("http://turis-ucuenca/ruta/"+rutaId);
              rutaResource.addProperty(RDF.type,myModel.createProperty(BASE,"/Route"));
              rutaResource.addProperty(RDF.type, OWL2.NamedIndividual);
              rutaResource.addProperty(DC.title, nuevaRutaInfo.getNombre());
              rutaResource.addProperty(RDFS.label,nuevaRutaInfo.getNombre());
              rutaResource.addProperty(DC.date,  myModel.createTypedLiteral(ZonedDateTime.now( ZoneOffset.UTC ).format( DateTimeFormatter.ISO_INSTANT ), "http://www.w3.org/2001/XMLSchema#dateTime"));
              rutaResource.addProperty(DC.description, nuevaRutaInfo.getDescripcion());
              rutaResource.addProperty(DC.creator,myModel.createResource(BASE+"/user/"+nuevaRutaInfo.getUserId()));
              Seq places = myModel.createSeq();
              for (int i = 0 ;i < nuevaRutaInfo.getLugares().size();i++){
                   places.add(i+1,myModel.createResource("http://turis-ucuenca/lugar/"+nuevaRutaInfo.getLugares().get(i)));
              }


              rutaResource.addProperty(myModel.createProperty(BASE,"/hasPlaces"),places);

              Resource userResource = myModel.createResource(BASE+"/user/"+nuevaRutaInfo.getUserId());
              userResource.addProperty(myModel.createProperty(BASE,"/hasRoute"),rutaResource);


              myModel.commit();
              return rutaId;
         }finally {
         }

    }
}




