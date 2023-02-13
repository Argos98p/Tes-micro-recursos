package com.turisup.resources.model.parser;

import com.complexible.common.base.Bool;
import com.turisup.resources.model.*;
import com.turisup.resources.utils.Utils;
import org.apache.jena.base.Sys;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.rdf.model.Literal;
import org.quartz.SimpleTrigger;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Arrays;

public class Parser {

    public static PlaceResponse QueryResult2Place(QuerySolution soln){
        PlaceResponse placeResponse = new PlaceResponse();

        System.out.println(soln);

        if (soln.getLiteral("orgName") == null){
            return null;
        }
        String orgName = soln.getLiteral("orgName").toString();
        String orgId= soln.getResource("org").getLocalName();

        String creadoPor= soln.getResource("creador").getLocalName();
        String nombreCreador=soln.getLiteral("nombre").toString();

        String region = soln.getLiteral("regionTitulo").toString();
        String regionId= soln.getResource("region").getLocalName();

        String placeId = soln.getResource("place").toString().replace("http://turis-ucuenca/lugar/","");

        String status = soln.getLiteral("status").toString();
        String titulo = soln.getLiteral("titulo").toString();
        String descripcion = soln.getLiteral("descripcion").toString();

        ArrayList<String> facebookImagesIds= new ArrayList( Arrays.asList( soln.getLiteral("fbIDs").toString().split(",") ) );
        facebookImagesIds.remove("http://www.w3.org/1999/02/22-rdf-syntax-ns#Bag");
        //facebookImagesIds.remove(facebookImagesIds.size()-1);

        ArrayList<String> facebookImagesUrls= new ArrayList( Arrays.asList( soln.getLiteral("imagenes").toString().split(",") ) );

        Point2D.Double point= Utils.literalToPoint(soln.getLiteral("point"));
        PlacePoint mypoint = new PlacePoint(point.x,point.y);

        PlaceResponse myPlaceResponse = new PlaceResponse(placeId,titulo,status,mypoint,descripcion,facebookImagesUrls,facebookImagesIds,new Organization(orgId,orgName),new Region(regionId,region),new User(creadoPor,nombreCreador));


        return myPlaceResponse;
    }
}
