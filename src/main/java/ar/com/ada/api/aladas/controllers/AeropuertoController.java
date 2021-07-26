package ar.com.ada.api.aladas.controllers;

import java.util.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import ar.com.ada.api.aladas.entities.Aeropuerto;
import ar.com.ada.api.aladas.models.response.GenericResponse;
import ar.com.ada.api.aladas.services.AeropuertoService;

@RestController
public class AeropuertoController {

    @Autowired
    AeropuertoService service;

    @PostMapping("/api/aeropuertos")
    public ResponseEntity<GenericResponse> crear(@RequestBody Aeropuerto aeropuerto){


        GenericResponse respuesta = new GenericResponse();
        service.crear(aeropuerto.getAeropuertoId(), aeropuerto.getNombre(), aeropuerto.getCodigoIATA());
        respuesta.isOk = true;
        respuesta.message = "Se creo correctamente";

        return ResponseEntity.ok(respuesta);
    }

    @GetMapping("/api/aeropuertos")
    public ResponseEntity<List<Aeropuerto>> obtenerTodos(){
        return ResponseEntity.ok(service.obtenerTodos());
    }
    
}
