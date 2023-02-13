package com.turisup.resources.service;

import com.complexible.stardog.api.Connection;
import com.complexible.stardog.jena.SDJenaFactory;
import com.turisup.resources.model.Organization;
import com.turisup.resources.model.Place;
import com.turisup.resources.model.PlaceResponse;
import com.turisup.resources.model.Region;
import com.turisup.resources.model.admin.AdminUserResource;
import com.turisup.resources.model.parser.Parser;
import com.turisup.resources.model.parser.QueryOptions;
import com.turisup.resources.model.request.get.AdminPlaceRequest;
import com.turisup.resources.model.request.get.AdminUser;
import com.turisup.resources.model.request.post.PlaceRequest;
import com.turisup.resources.repository.DBConnection;
import com.turisup.resources.repository.SparqlTemplates;
import com.turisup.resources.repository.StardogHttpQueryConn;
import org.apache.jena.query.*;
import org.apache.jena.rdf.model.Model;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

import static com.turisup.resources.repository.FBConnection.getImagesSrcById;
import static com.turisup.resources.repository.SparqlTemplates.updateImagesUrls;
import static com.turisup.resources.repository.SparqlTemplates.updatePlaceQuery;

@Service
public class AdminService {
    @Autowired
    PlaceService placeService;
    StardogHttpQueryConn stardogHttpQueryConn;

    public List<Place> placesByRegion(AdminPlaceRequest adminPlaceRequest) {
        List<Place> places = new ArrayList<>();
        try (Connection myConnection = DBConnection.createConnection()) {
            Model myModel = SDJenaFactory.createModel(myConnection);


            String queryString = SparqlTemplates.getPlacesByRegionOrgStatusUser(adminPlaceRequest);

            Query query = QueryFactory.create(queryString);
            QueryExecution qexec = QueryExecutionFactory.create(query, myModel);
            try {
                ResultSet results = qexec.execSelect();
                while (results.hasNext()) {
                    QuerySolution soln = results.nextSolution();
                    //myNewPlace= Parser.QueryResult2Place(soln,true);
                    // myNewPlace.setId(placeId);
                }
            } finally {
                qexec.close();
            }
        }
        return places;
    }


    public ResponseEntity<?> updatePlace(PlaceRequest placeUpdateInfo) {
        String query = updatePlaceQuery(placeUpdateInfo);
        query= query.replaceAll("\n"," ");
        stardogHttpQueryConn = new StardogHttpQueryConn();
        return stardogHttpQueryConn.PostToTriplestore(query);
    }

    public List<AdminUserResource> userAdminResources(String userId) {
        List<AdminUserResource> resources = new ArrayList<>();
        try(Connection myConnection = DBConnection.createConnection()){
            Model myModel = SDJenaFactory.createModel(myConnection);


            String queryString = SparqlTemplates.getUserAdminResources(userId);
            Query query = QueryFactory.create(queryString);
            QueryExecution qexec = QueryExecutionFactory.create(query,myModel);
            try {
                ResultSet results= qexec.execSelect();
                while(results.hasNext()){
                    QuerySolution soln = results.nextSolution();
                    Organization myOrg = new Organization(soln.getResource("org").getLocalName(),soln.getLiteral("orgTitle").toString());
                    Region myRegion= new Region(soln.getResource("region").getLocalName(),soln.getLiteral("regionTitle").toString());
                    resources.add(new AdminUserResource(myOrg,myRegion));
                }
            }finally {
                qexec.close();
            }
        }
        return  resources;
    }

    public ResponseEntity<?> actualizarImagenesFB() {
        QueryOptions queryOptions = new QueryOptions();
        List<PlaceResponse> places = placeService.all(queryOptions);

        for(PlaceResponse placeResponse: places){

            List<String> imagesUrls = getImagesSrcById(placeResponse.getFbImagenesIds());

            String queryUpdate = updateImagesUrls(imagesUrls,placeResponse.getId());

            stardogHttpQueryConn = new StardogHttpQueryConn();
            stardogHttpQueryConn.PostToTriplestore(queryUpdate);
        }


        return new ResponseEntity<>(HttpStatus.OK);
    }


    public List<AdminUser> getUsersInOrg(String orgId) {
        List<AdminUser> usuarios = new ArrayList<>();
        try(Connection myConnection = DBConnection.createConnection()){
            Model myModel = SDJenaFactory.createModel(myConnection);


            String queryString = SparqlTemplates.usersInOrg(orgId);
            System.out.println(queryString);
            Query query = QueryFactory.create(queryString);

            QueryExecution qexec = QueryExecutionFactory.create(query,myModel);
            try {
                ResultSet results= qexec.execSelect();
                while(results.hasNext()){
                    QuerySolution soln = results.nextSolution();
                    AdminUser user = new AdminUser(soln.getLiteral("nombre").toString(),soln.getLiteral("nick").toString(),soln.getLiteral("correoM").toString(),soln.getLiteral("userM").toString());
                    usuarios.add(user);
                }
            }finally {
                qexec.close();
            }
        }
        return usuarios;
    }
}