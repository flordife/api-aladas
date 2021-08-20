package ar.com.ada.api.aladas.controllers;

import java.util.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import ar.com.ada.api.aladas.entities.*;
import ar.com.ada.api.aladas.entities.Usuario.TipoUsuarioEnum;
import ar.com.ada.api.aladas.models.response.GenericResponse;
import ar.com.ada.api.aladas.services.*;
import ar.com.ada.api.aladas.services.AeropuertoService.ValidacionAeropuertoDataEnum;

@RestController
public class AeropuertoController {

    @Autowired
    AeropuertoService service;

    @Autowired
    UsuarioService usuarioService;

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

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        Usuario usuario = usuarioService.buscarPorUsername(username);
        
        if (usuario.getTipoUsuario() == TipoUsuarioEnum.STAFF){
            Aeropuerto aeropuerto = service.buscarPorCodigoIATA(codigoIATA);
            if (aeropuerto == null) {
                return ResponseEntity.badRequest().build();
            }

            return ResponseEntity.ok(aeropuerto);
        } else {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }
}
