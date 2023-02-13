package com.turisup.resources.repository;

import com.turisup.resources.model.parser.QueryOptions;
import com.turisup.resources.model.request.get.AdminPlaceRequest;
import com.turisup.resources.model.request.post.PlaceRequest;
import org.checkerframework.checker.units.qual.Prefix;

import java.util.List;

public class SparqlTemplates {

    static final String prefixes = "prefix : <http://turis-ucuenca/>"+
            "prefix rdf:<http://www.w3.org/1999/02/22-rdf-syntax-ns#>"+
            "prefix org: <http://www.w3.org/TR/vocab-org/>"+
            "prefix myorg: <http://turis-ucuenca/org/>"+
            "prefix myregiones: <http://turis-ucuenca/region/>"+
            "prefix lugar: <http://turis-ucuenca/lugar/>"+
            "prefix myusers: <http://turis-ucuenca/user/>"+
            "prefix foaf: <http://xmlns.com/foaf/0.1/>"+
            "prefix tp: <http://tour-pedia.org/download/tp.owl>"+
            "prefix vcard: <http://www.w3.org/2006/vcard/ns#>"+
            "prefix geo: <http://www.opengis.net/ont/geosparql#>"+
            "prefix dc: <http://purl.org/dc/elements/1.1/>"+
            "prefix base2:<http://turis-ucuenca#>"+
            "prefix baseProperty: <http://turis-ucuenca#>" +
            "prefix base3:<http://turis-ucuenca/>"+
            "prefix fb:<http://turis-ucuenca#>"+
            "base  <http://turis-ucuenca/>";

    public static String getPlace(String placeId){
        String lugar = "lugar:"+placeId;
            return prefixes+"" +
                    "SELECT  ?titulo ?descripcion (GROUP_CONCAT(DISTINCT ?imagenes2 ; SEPARATOR = ',') AS ?imagenes)  ?creador ?point  (GROUP_CONCAT(DISTINCT ?idFacebook ; SEPARATOR = ',') AS ?fbIDs)  WHERE{"+
                    lugar +" dc:title ?titulo."+
                    lugar +" dc:description ?descripcion."+
                    lugar +" fb:facebookId ?faceboook_id_node."+
                    lugar +" dc:creator ?creador."+
                    lugar +" vcard:hasPhoto ?imagenes2."+
                    lugar +" geo:hasGeometry ?geometry_node."+
                    "?geometry_node geo:asWKT ?point."+
                    "?faceboook_id_node ?prop ?idFacebook."+
                    "FILTER(?prop != rdf:type)"+
                    "} GROUP BY ?titulo ?descripcion ?creador ?point";
    }

    public static String getAllPlace() {
        return prefixes+"" +
                "SELECT ?id ?titulo ?descripcion (GROUP_CONCAT(DISTINCT ?imagenes2 ; SEPARATOR = ',') AS ?imagenes)  ?creador ?point  (GROUP_CONCAT(DISTINCT ?idFacebook ; SEPARATOR = ',') AS ?fbIDs)  WHERE{"+
                "?id a tp:POI."+
                "?id dc:title ?titulo."+
                "?id dc:description ?descripcion."+
                "?id fb:facebookId ?faceboook_id_node."+
                "?id dc:creator ?creador."+
                "?id vcard:hasPhoto ?imagenes2."+
                "?id geo:hasGeometry ?geometry_node."+
                "?geometry_node geo:asWKT ?point."+
                "?faceboook_id_node ?prop ?idFacebook."+
                "FILTER(?prop != rdf:type)"+
                "} GROUP BY ?id ?titulo ?descripcion ?creador ?point";
    }

    public static String getPlacesByRegionOrgStatusUser(AdminPlaceRequest props) {

        return "";
    }

    public static String updateImagesUrls(List<String> newImagesUrls,String placeId){
        StringBuilder insert= new StringBuilder();
        for(String url:newImagesUrls){
            insert.append("\n").append("lugar:").append(placeId).append(" vcard:hasPhoto \"").append(url).append("\".");
        }
        return prefixes+
                "DELETE{"
                + "?place vcard:hasPhoto ?photo."
                + "}"
                + "INSERT{"
                    + insert
                + "}"
                + " WHERE{"
                    + "?place vcard:hasPhoto ?photo."
                    + "FILTER(str(?place) = 'http://turis-ucuenca/lugar/"+placeId+"')"
                + " }";
    }
    public static String getUserAdminResources(String userId) {
        String userUri= "http://turis-ucuenca/user/"+userId;
        return prefixes+
                "SELECT * WHERE {"+
                "?user a foaf:Person."+
                "?user foaf:memberOf ?org."+

                "?org dc:title ?orgTitle."+
                "?org :admin ?region."+

                "?region dc:title ?regionTitle."+

                "FILTER(str(?user)='"+userUri+"')"+
                "}";
    }
    public static String  defaultQuery(QueryOptions queryOptions){

        String filters ="";
        if(queryOptions.getCreadorId()!= null){
            filters = filters + "FILTER(str(?creador)= 'http://turis-ucuenca/user/"+queryOptions.getCreadorId()+"').\n";
        }if(queryOptions.getEstadoLugar()!= null){
            filters = filters +"FILTER(str(?status) = '"+queryOptions.getEstadoLugar()+"').\n";
        }if(queryOptions.getLugarId()!= null){
            filters = filters + "FILTER(str(?place)='http://turis-ucuenca/lugar/"+queryOptions.getLugarId()+"').\n";
        }if(queryOptions.getOrganizacionId()!=null){
            filters = filters +  "FILTER(str(?org)='http://turis-ucuenca/org/"+queryOptions.getOrganizacionId()+"').\n";
        }if(queryOptions.getRegionId()!=null){
            filters = filters + "FILTER(str(?region)='http://turis-ucuenca/region/"+queryOptions.getRegionId()+"').\n";
        }

        return "prefix : <http://turis-ucuenca/>"+
                "prefix org: <http://www.w3.org/TR/vocab-org/>"+
                "prefix myorg: <http://turis-ucuenca/org/>"+
                "prefix myregiones: <http://turis-ucuenca/region/>"+
                "prefix lugar: <http://turis-ucuenca/lugar/>"+
                "prefix myusers: <http://turis-ucuenca/user/>"+
                "prefix foaf: <http://xmlns.com/foaf/0.1/>"+
                "prefix tp: <http://tour-pedia.org/download/tp.owl>"+
                "prefix vcard: <http://www.w3.org/2006/vcard/ns#>"+
                "prefix geo: <http://www.opengis.net/ont/geosparql#>"+
                "prefix dc: <http://purl.org/dc/elements/1.1/>"+
                "prefix fb:<http://turis-ucuenca#>"+
                "prefix base2:<http://turis-ucuenca#>"+
                "prefix geof: <http://www.opengis.net/def/function/geosparql/>"+
                "base  <http://turis-ucuenca/>"+

                "SELECT ?org ?orgName ?region ?regionTitulo ?place ?titulo ?status ?descripcion (GROUP_CONCAT(DISTINCT ?imagenes2 ; SEPARATOR = ',') AS ?imagenes)  ?creador ?nombre ?point  (GROUP_CONCAT(DISTINCT ?idFacebook ; SEPARATOR = ',') AS ?fbIDs)"+
                "WHERE {"+
                "?region a :Region."+
                "?region dc:title ?regionTitulo."+
                "?region geo:hasGeometry ?geoRegion."+
                "?region :isAdminBy ?org."+
                "?geoRegion geo:asWKT ?regionWKT ."+
                "?org dc:title ?orgName."+
                "?place a tp:POI ."+
                "?place dc:title ?titulo."+
                "?place dc:description ?descripcion."+
                "?place fb:facebookId ?faceboook_id_node."+
                "?place dc:creator ?creador."+
                "?place vcard:hasPhoto ?imagenes2."+
                "?place base2:status ?status."+
                "?place geo:hasGeometry ?geom."+

                "?creador foaf:name ?nombre."+

                "?faceboook_id_node ?prop ?idFacebook."+
                "?geom geo:asWKT ?point ."+

                "FILTER geof:within(?point,?regionWKT)."+
                filters+

                "} GROUP BY ?org ?orgName ?region ?regionTitulo ?place ?titulo ?status ?descripcion ?creador ?point ?nombre";
    }

    public static String usersInOrg (String orgId){
        String aux = "\"http://turis-ucuenca/org/"+orgId+"\")}";
        return prefixes+
                " SELECT ?nombre ?nick ?correoM ?userM WHERE{"+
    " ?org a org:Organization."+
                    " ?user a foaf:Person."+
                    " ?user base3:role 'admin'."+
                    " ?user foaf:memberOf ?org."+
                    " ?user foaf:name ?nombre."+
                    " ?user  foaf:nick ?nick."+
                    " ?user foaf:mbox ?correo."+
                    " BIND(REPLACE(STR(?user),'http://turis-ucuenca/user/','') AS ?userM)."+
                    " BIND(REPLACE(STR(?correo),'http://turis-ucuenca/','') AS ?correoM) ."+

            " FILTER(str(?org) ="+ aux;
        }

    public static String rutasByUser(String userId){

        return prefixes+
                "SELECT  * where{"+
    "<http://turis-ucuenca/user/"+userId+"> :hasRoute ?ruta."+
                                   "?ruta dc:title ?nombre;"+

        "}";
    }

    public static  String updatePlaceQuery(PlaceRequest placeUpdateInfo){
        String placeid = "'http://turis-ucuenca/lugar/"+placeUpdateInfo.getPlaceid()+"'";
        return prefixes

                +"DELETE{"
    +"?place dc:title ?titulo;"
           + "dc:description ?descripcion;"
           + "base2:status ?status;"
           + "rdfs:label ?titulo2;"
       + "}"
       + "INSERT{"
        +    "lugar:"+placeUpdateInfo.getPlaceid()+" dc:title '"+ placeUpdateInfo.getNombre()+"';"
        +    "dc:description '"+placeUpdateInfo.getDescripcion()+"';"
       +     "base2:status '"+placeUpdateInfo.getEstado()+"';"
       +     "rdfs:label '"+placeUpdateInfo.getNombre()+"'."
       + "}"
        +"WHERE{"
   +"?place dc:title ?titulo;"
         +   "dc:description ?descripcion;"
        +    "base2:status ?status;"
        +    "rdfs:label ?titulo2;"
       +     "FILTER(str(?place) = "+placeid+")"
      +  "}";
    }

    public static String getRuta(String rutaId) {
        return  prefixes +
                "SELECT  ?nombre ?descripcion ?creador ?placeNode {\n" +
                "    <http://turis-ucuenca/ruta/"+rutaId+"> dc:title ?nombre;\n" +
                "                                                                     dc:description ?descripcion;\n" +
                "                                                                     dc:creator ?creador;\n" +
                "                                                                     :hasPlaces ?placeNode.\n" +
                "}";
    }


    public static String getRutaPlaces(String node_id){
        return prefixes+
                "SELECT  ?placeId ?nombre ?descripcion (GROUP_CONCAT(DISTINCT ?imagenes2 ; SEPARATOR = ',') AS ?imagenes) ?point ?creador{\n" +
                "    <_:"+node_id+"> ?g ?placeId.\n" +
                "    ?placeId dc:title ?nombre.\n" +
                "    ?placeId dc:description ?descripcion.\n" +
                "    ?placeId dc:creator ?creador.               \n" +
                "    ?placeId vcard:hasPhoto ?imagenes2.    \n" +
                "    ?placeId geo:hasGeometry ?geometry_node.      \n" +
                "    ?geometry_node geo:asWKT ?point.       \n" +
                "} GROUP BY ?placeId ?nombre ?descripcion ?point ?creador";
    }

    public static String getComentsInPlace(String idPlace){
       return  prefixes+ "\nSELECT ?user ?nombreUser ?fotoUser ?com ?comentario ?puntaje (GROUP_CONCAT(DISTINCT ?img ; SEPARATOR = ',') AS ?imagenes) WHERE{\n" +
                "    ?user a foaf:Person.\n" +
                "    ?user foaf:name ?nombreUser.\n" +
                "    ?user foaf:depiction ?fotoUser.\n" +
                "    ?user baseProperty:comment ?com.\n" +
                "    ?com a :Comentario.\n" +
                "    ?com baseProperty:place <http://turis-ucuenca/lugar/"+idPlace+">.\n" +
                "    ?com baseProperty:text ?comentario.\n" +
                "    ?com baseProperty:rating ?puntaje.\n" +
                "\n" +
                "    OPTIONAL { ?com vcard:hasPhoto ?img }    \n" +
                "} GROUP BY ?user ?nombreUser ?fotoUser ?com ?comentario ?puntaje";
    }

    public static String deleteComent(String comentarioId) {
        return prefixes+"\nDELETE {\n" +
                "    ?user baseProperty:comment <http://turis-ucuenca/comentario/"+comentarioId+">.\n" +
                "    <http://turis-ucuenca/comentario/"+comentarioId+"> ?f ?b.\n" +
                "    \n" +
                "}WHERE{\n" +
                "    ?user baseProperty:comment <http://turis-ucuenca/comentario/"+comentarioId+">.\n" +
                "    <http://turis-ucuenca/comentario/"+comentarioId+"> ?f ?b.\n" +
                "}";
    }
}
