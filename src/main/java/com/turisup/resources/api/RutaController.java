package com.turisup.resources.api;

import com.google.api.Http;
import com.turisup.resources.model.PlaceResponse;
import com.turisup.resources.service.PlaceService;
import com.turisup.resources.service.RutaService;
import org.json.HTTP;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/ruta")
public class RutaController {

    @Autowired
    RutaService rutaService;

    @GetMapping("")
    public ResponseEntity <ArrayList<Map<String,String>>> getRutasByUser (@RequestParam("userId") String userId){
        ArrayList<Map<String,String>> rutasDelUsuario = rutaService.getRutasUser(userId);

        return  ResponseEntity.ok().body(rutasDelUsuario);
    }

    @GetMapping("/id")
    public  ResponseEntity<?> getRoute(@RequestParam("rutaId") String rutaId){
        Map<String,Object> ruta = rutaService.getOneRuta(rutaId);
        if(ruta.isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Ruta no encontrada");
        }
        return ResponseEntity.ok().body(ruta);

    }
}
