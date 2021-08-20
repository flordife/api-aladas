package ar.com.ada.api.aladas.services;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ar.com.ada.api.aladas.entities.*;
import ar.com.ada.api.aladas.entities.Reserva.EstadoReservaEnum;
import ar.com.ada.api.aladas.entities.Vuelo.EstadoVueloEnum;
import ar.com.ada.api.aladas.repos.ReservaRepository;

@Service
public class ReservaService {

    @Autowired
    ReservaRepository repo;

    @Autowired
    VueloService vueloService;

    @Autowired
    PasajeroService pasajeroService;

    public Reserva generarReserva(Integer vueloId, Integer pasajeroId) {

        Reserva reserva = new Reserva();

        Vuelo vuelo = vueloService.buscarPorId(vueloId);
        vuelo.agregarReserva(reserva);
        // La fecha de emisión de la reserva, no del pasaje
        reserva.setFechaEmision(new Date());

        // Se crea un Calendario y se le suma un día
        Calendar c = Calendar.getInstance();
        c.setTime(reserva.getFechaEmision());
        c.add(Calendar.DATE, 1);

        reserva.setFechaVencimiento(c.getTime());

        reserva.setEstadoReservaId(EstadoReservaEnum.CREADA);

        Pasajero pasajero = pasajeroService.buscarPorId(pasajeroId);

        // Relaciones bidireccionales
        pasajero.agregarReserva(reserva);
        vuelo.agregarReserva(reserva);

        return repo.save(reserva);
    }

    public Reserva buscarPorId(Integer id) {
        return repo.findByReservaId(id);
    }

    public ValidacionReservaDataEnum validarReserva(Integer vueloId) {
        if (!validarVueloAbierto(vueloId)) {
            return ValidacionReservaDataEnum.ERROR_VUELO_NO_ABIERTO;
        }
        if (!validarCapacidadMaxima(vueloId)) {
            return ValidacionReservaDataEnum.ERROR_VUELO_LLENO;
        } else {
            return ValidacionReservaDataEnum.OK;
        }
    }

    public enum ValidacionReservaDataEnum {
        OK, ERROR_VUELO_NO_ABIERTO, ERROR_VUELO_LLENO;
    }

    public boolean validarVueloAbierto(Integer vueloId) {
        Vuelo vuelo = vueloService.buscarPorId(vueloId);
        if (vuelo.getEstadoVueloId().equals(EstadoVueloEnum.ABIERTO)) {
            return true;
        } else {
            return false;
        }
    }

    public boolean validarCapacidadMaxima(Integer vueloId) {
        Vuelo vuelo = vueloService.buscarPorId(vueloId);
        if (vuelo.getCapacidad() > 0) {
            return true;
        } else {
            return false;
        }
    }

    public List<Reserva> traerReservasPorVuelo(Integer vueloId) {

        Vuelo vuelo = vueloService.buscarPorId(vueloId);

        return vuelo.getReservas();
    }

}
