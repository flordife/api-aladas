package ar.com.ada.api.aladas.controllers;

import java.util.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import ar.com.ada.api.aladas.entities.Aeropuerto;
import ar.com.ada.api.aladas.models.response.GenericResponse;
import ar.com.ada.api.aladas.services.AeropuertoService;
import ar.com.ada.api.aladas.services.AeropuertoService.ValidacionAeropuertoDataEnum;

@RestController
public class AeropuertoController {

    @Autowired
    AeropuertoService service;

    @PostMapping("/api/aeropuertos")
    public ResponseEntity<GenericResponse> crear(@RequestBody Aeropuerto aeropuerto) {

        GenericResponse respuesta = new GenericResponse();

        ValidacionAeropuertoDataEnum resultadoValidacion = service.validar(aeropuerto);
        if (resultadoValidacion == ValidacionAeropuertoDataEnum.OK) {
            service.crear(aeropuerto.getAeropuertoId(), aeropuerto.getNombre(), aeropuerto.getCodigoIATA());

            respuesta.isOk = true;
            respuesta.message = "Se creo correctamente";

            return ResponseEntity.ok(respuesta);
        } else {
            respuesta.isOk = false;
            respuesta.message = "Error(" + resultadoValidacion.toString() + ")";

            return ResponseEntity.badRequest().body(respuesta);
        }
    }

    @GetMapping("/api/aeropuertos")
    public ResponseEntity<List<Aeropuerto>> obtenerTodos() {
        return ResponseEntity.ok(service.obtenerTodos());
    }

    @GetMapping("api/aeropuertos/{codigoIATA}")
    public ResponseEntity<Aeropuerto> obtenerAeropuertoPorCodigoIATA(@PathVariable String codigoIATA) {
        Aeropuerto aeropuerto = service.buscarPorCodigoIATA(codigoIATA);
        if (aeropuerto == null) {
            return ResponseEntity.badRequest().build();
        }

        return ResponseEntity.ok(aeropuerto);
    }

    // NO FUNCIONA - VERIFICAR QUÉ PASA
    @GetMapping("api/aeropuertos/{id}")
    public ResponseEntity<Aeropuerto> obtenerAeropuertoPorId(@PathVariable Integer id) {
        Aeropuerto aeropuerto = service.buscarPorAeropuertoId(id);
        if (aeropuerto == null) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(aeropuerto);
    }

}
