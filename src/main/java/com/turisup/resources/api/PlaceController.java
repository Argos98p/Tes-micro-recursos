package com.turisup.resources.api;

import com.complexible.common.base.Bool;
import com.google.gson.Gson;
import com.turisup.resources.model.Place;
import com.turisup.resources.model.PlaceResponse;
import com.turisup.resources.model.parser.QueryOptions;
import com.turisup.resources.model.request.post.AddRoute;
import com.turisup.resources.model.request.post.PlaceRequest;
import com.turisup.resources.service.PlaceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.springframework.web.bind.annotation.RequestMethod.POST;

@RestController
@RequestMapping("/api/recurso")
public class PlaceController {

    @Autowired
    PlaceService placeService;

    @RequestMapping(path = "/nuevo", method = POST, consumes = { MediaType.MULTIPART_FORM_DATA_VALUE })
    public ResponseEntity <?> newPlace(@RequestParam("recurso") String jsonString, @RequestParam("files")MultipartFile[] files){
        Gson g = new Gson();
        PlaceRequest placeRequest = g.fromJson(jsonString, PlaceRequest.class);
        Place myPlace =  placeService.save(placeRequest,files);
        return ResponseEntity.ok().body(myPlace);
    }

    @PostMapping("/nuevaRuta")
    public ResponseEntity<?> newRoute (@RequestBody (required=true) AddRoute nuevaRutaInfo){
        String newRouteId = placeService.addRoute(nuevaRutaInfo);
        if(newRouteId != null){
            return ResponseEntity.ok().body(newRouteId);
        }
        return ResponseEntity.badRequest().body("error");
    }



    @GetMapping()
    public ResponseEntity<PlaceResponse> getPlace (@RequestParam("recursoId") String placeId){
       PlaceResponse place= placeService.get(placeId);
       return  ResponseEntity.ok().body(place);
    }

    @GetMapping("/todos")
    public ResponseEntity<List<PlaceResponse>> allPlaces(
            @RequestParam(name="organizacionId") Optional<String> organizacionId,
            @RequestParam(name="regionId") Optional<String>  regionId,
            @RequestParam(name="creadorId") Optional<String>  creadorId,
            @RequestParam(name="lugarId") Optional<String>  lugarId,
            @RequestParam(name="estadoLugar") Optional<String>  estadoLugar
    ){

        QueryOptions queryOptions = new QueryOptions();
        if (organizacionId.isPresent()) {
            queryOptions.setOrganizacionId(organizacionId.get());
        } else {
            queryOptions.setOrganizacionId(null);
        }
        if (regionId.isPresent()) {
            queryOptions.setRegionId(regionId.get());
        } else {
            queryOptions.setRegionId(null);
        }
        if (creadorId.isPresent()) {
            queryOptions.setCreadorId(creadorId.get());
        } else {
            queryOptions.setCreadorId(null);
        }
        if (lugarId.isPresent()) {
            queryOptions.setLugarId(lugarId.get());
        } else {
            queryOptions.setLugarId(null);
        }if (estadoLugar.isPresent()) {
            queryOptions.setEstadoLugar(estadoLugar.get());
        } else {
            queryOptions.setEstadoLugar(null);
        }


        List<PlaceResponse> places = placeService.all( queryOptions);
        //
        return ResponseEntity.ok().body(places);
    }


}
