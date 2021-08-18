package ar.com.ada.api.aladas.services;

import java.util.Calendar;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ar.com.ada.api.aladas.entities.*;
import ar.com.ada.api.aladas.entities.Reserva.EstadoReservaEnum;
import ar.com.ada.api.aladas.repos.ReservaRepository;

@Service
public class ReservaService {

    @Autowired
    ReservaRepository repo;

    @Autowired
    VueloService vueloService;

    @Autowired
    PasajeroService pasajeroService;

    public Integer generarReserva(Integer vueloId, Integer pasajeroId) {

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

        repo.save(reserva);

        return reserva.getReservaId();
    }

    public Reserva buscarPorId(Integer id) {
        return repo.findByReservaId(id);
    }

}
