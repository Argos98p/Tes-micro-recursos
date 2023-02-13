package com.turisup.resources.utils;

import org.apache.jena.rdf.model.Literal;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.XML;

import java.awt.geom.Point2D;

public class Utils {

    public static Point2D.Double literalToPoint(Literal pointLiteral){
        String myPoint = pointLiteral.toString();
        String aux = myPoint.substring(myPoint.indexOf("(")+1, myPoint.indexOf(")"));
        String [] aux2 = aux.split(" ");
        return new Point2D.Double(Double.parseDouble(aux2[0]), Double.parseDouble(aux2[1]));
    }

    public static String convertXMLtoJSON(String xml){
        System.out.println(xml);
        try {
            JSONObject json = XML.toJSONObject(xml);
            String jsonString = json.toString(4);
            System.out.println(jsonString);
            return jsonString;

        }catch (JSONException e) {
// TODO: handle exception
            System.out.println(e.toString());
            return "error";
        }

    }
}
