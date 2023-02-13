package com.turisup.resources.model;

import lombok.Data;
import java.util.ArrayList;

@Data
public class Place {
    String id;
    String nombre;

    PlacePoint coordenadas;
    String descripcion;
    String usuarioId;
    ArrayList<String> imagenesPaths;
    ArrayList<String> fbImagenesIds;

    public Place(String id,String nombre, PlacePoint coordenadas,  String descripcion, String usuarioId) {
        this.id = id;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.usuarioId = usuarioId;
        this.coordenadas=coordenadas;
    }

    public Place() {

    }



}
