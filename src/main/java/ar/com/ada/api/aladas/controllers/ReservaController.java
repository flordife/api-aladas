package ar.com.ada.api.aladas.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import ar.com.ada.api.aladas.entities.Reserva;
import ar.com.ada.api.aladas.entities.Usuario;
import ar.com.ada.api.aladas.entities.Usuario.TipoUsuarioEnum;
import ar.com.ada.api.aladas.models.request.InfoReservaNueva;
import ar.com.ada.api.aladas.models.response.GenericResponse;
import ar.com.ada.api.aladas.models.response.ReservaResponse;
import ar.com.ada.api.aladas.services.ReservaService;
import ar.com.ada.api.aladas.services.UsuarioService;
import ar.com.ada.api.aladas.services.ReservaService.ValidacionReservaDataEnum;

@RestController
public class ReservaController {

    @Autowired
    ReservaService service;

    @Autowired
    UsuarioService usuarioService;

    @PostMapping("/api/reservas")
    public ResponseEntity<?> generarReserva(@RequestBody InfoReservaNueva infoReserva) {
        ReservaResponse rta = new ReservaResponse();

        // Obtengo a quien esta autenticado del otro lado
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        // De lo que esta autenticado, obtengo su USERNAME
        String username = authentication.getName();
        // Buscar el usuario por username
        Usuario usuario = usuarioService.buscarPorUsername(username);

        if (usuario.getTipoUsuario() == TipoUsuarioEnum.PASAJERO) {

            ValidacionReservaDataEnum resultado = service.validarReserva(infoReserva.vueloId);
            if (resultado == ValidacionReservaDataEnum.OK) {
                // con el usuario, obtengo el pasajero, y con ese, obtengo el Id
                Reserva reserva = service.generarReserva(infoReserva.vueloId, usuario.getPasajero().getPasajeroId());

                rta.fechaEmision = reserva.getFechaEmision();
                rta.fechaVencimiento = reserva.getFechaEmision();
                rta.reservaId = reserva.getReservaId();
                rta.message = "Reserva fue creada exitosamente.";

                return ResponseEntity.ok(rta);
            } else {
                GenericResponse respuesta = new GenericResponse();
                respuesta.isOk = false;
                respuesta.message = "Error(" + resultado.toString() + ")";

                return ResponseEntity.badRequest().body(respuesta);
            }
        } else {

            GenericResponse respuesta = new GenericResponse();
            respuesta.isOk = false;
            respuesta.message = "Deberá loguearse como Pasajero para reservar";

            return ResponseEntity.badRequest().body(respuesta);
        }

    }

    @GetMapping("api/reservas/vuelos/{vueloId}")
    public ResponseEntity<List<Reserva>> obtenerReservasPorVuelo(@PathVariable Integer vueloId) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        Usuario usuario = usuarioService.buscarPorUsername(username);

        if (usuario.getTipoUsuario() == TipoUsuarioEnum.STAFF) {
            List<Reserva> reservas = service.traerReservasPorVuelo(vueloId);
            return ResponseEntity.ok(reservas);
        } else {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }

}
