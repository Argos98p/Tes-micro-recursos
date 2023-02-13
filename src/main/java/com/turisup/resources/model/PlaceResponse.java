package com.turisup.resources.model;

import lombok.Data;

import java.util.ArrayList;

@Data
public class PlaceResponse {
    String id;
    String nombre;
    String status;
    PlacePoint coordenadas;
    String descripcion;
    ArrayList<String> imagenesPaths;
    ArrayList<String> fbImagenesIds;
    Organization organizacion;
    Region region;
    User user;

    public PlaceResponse(String id, String nombre,String status, PlacePoint coordenadas, String descripcion, ArrayList<String> imagenesPaths, ArrayList<String> fbImagenesIds, Organization organizacion, Region region, User user) {
        this.id = id;
        this.nombre = nombre;
        this.status = status;
        this.coordenadas = coordenadas;
        this.descripcion = descripcion;
        this.imagenesPaths = imagenesPaths;
        this.fbImagenesIds = fbImagenesIds;
        this.organizacion = organizacion;
        this.region = region;
        this.user = user;
    }

    public PlaceResponse() {

    }
}

