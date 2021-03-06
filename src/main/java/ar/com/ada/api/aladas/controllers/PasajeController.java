package ar.com.ada.api.aladas.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import ar.com.ada.api.aladas.entities.Pasaje;
import ar.com.ada.api.aladas.models.request.InfoPasajeNuevo;
import ar.com.ada.api.aladas.models.response.GenericResponse;
import ar.com.ada.api.aladas.models.response.PasajeResponse;
import ar.com.ada.api.aladas.services.PasajeService;
import ar.com.ada.api.aladas.services.PasajeService.ValidacionPasajeDataEnum;

@RestController
public class PasajeController {

    @Autowired
    PasajeService service;

    @PostMapping("api/pasajes")
    public ResponseEntity<?> emitir(@RequestBody InfoPasajeNuevo infoPasajes) {

        PasajeResponse respuesta = new PasajeResponse();

        ValidacionPasajeDataEnum resultado = service.validar(infoPasajes.reservaId);

        if (resultado == ValidacionPasajeDataEnum.OK) {
            Pasaje pasaje = service.emitir(infoPasajes.reservaId);

            respuesta.fechaDeEmision = pasaje.getFechaEmision();
            respuesta.infoDePago = "Abonado";
            respuesta.message = "El pasaje ha sido emitido correctamente";
            respuesta.reservaId = pasaje.getReserva().getReservaId();
            respuesta.vueloId = pasaje.getReserva().getVuelo().getVueloId();

            return ResponseEntity.ok(respuesta);
        } else {
            GenericResponse rta = new GenericResponse();
            rta.isOk = false;
            rta.message = "Error(" + resultado.toString() + ")";

            return ResponseEntity.badRequest().body(rta);
        }
    }
}