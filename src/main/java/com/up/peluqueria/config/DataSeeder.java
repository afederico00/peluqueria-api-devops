package com.up.peluqueria.config;

import com.up.peluqueria.entity.EstadoTurno;
import com.up.peluqueria.entity.Peluquero;
import com.up.peluqueria.entity.Turno;
import com.up.peluqueria.repository.PeluqueroRepository;
import com.up.peluqueria.repository.TurnoRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;

/**
 * Carga datos de ejemplo al iniciar la aplicación si la base está vacía,
 * replicando el seed de la aplicación Node original (src/app.ts).
 */
@Component
@Slf4j
public class DataSeeder implements CommandLineRunner {

    @Autowired
    private PeluqueroRepository peluqueroRepository;

    @Autowired
    private TurnoRepository turnoRepository;

    @Override
    public void run(String... args) {
        if (peluqueroRepository.count() == 0) {
            peluqueroRepository.saveAll(List.of(
                    Peluquero.builder().name("Tobias").build(),
                    Peluquero.builder().name("Santiago").build(),
                    Peluquero.builder().name("Mateo").build()
            ));
            log.info("Seed: peluqueros creados");
        }

        if (turnoRepository.count() == 0) {
            Peluquero tobias = peluqueroRepository.findByName("Tobias").orElseThrow();
            Peluquero santiago = peluqueroRepository.findByName("Santiago").orElseThrow();
            Peluquero mateo = peluqueroRepository.findByName("Mateo").orElseThrow();

            String fecha1 = siguienteDiaValido(5);
            String fecha2 = siguienteDiaValido(7);

            turnoRepository.saveAll(List.of(
                    Turno.builder().fecha(fecha1).hora("09:00").estadoTurno(EstadoTurno.RESERVADO).peluquero(tobias).build(),
                    Turno.builder().fecha(fecha1).hora("10:30").estadoTurno(EstadoTurno.RESERVADO).peluquero(tobias).build(),
                    Turno.builder().fecha(fecha1).hora("11:00").estadoTurno(EstadoTurno.FINALIZADO).peluquero(santiago).build(),
                    Turno.builder().fecha(fecha2).hora("09:30").estadoTurno(EstadoTurno.RESERVADO).peluquero(santiago).build(),
                    Turno.builder().fecha(fecha2).hora("14:00").estadoTurno(EstadoTurno.RESERVADO).peluquero(mateo).build(),
                    Turno.builder().fecha(fecha2).hora("15:30").estadoTurno(EstadoTurno.FINALIZADO).peluquero(mateo).build()
            ));
            log.info("Seed: turnos creados");
        }
    }

    /**
     * Avanza `diasAdelante` días desde hoy y luego salta domingos/lunes,
     * igual que la función nextValidDay de la aplicación Node original.
     */
    private String siguienteDiaValido(int diasAdelante) {
        LocalDate fecha = LocalDate.now().plusDays(diasAdelante);
        while (fecha.getDayOfWeek() == DayOfWeek.SUNDAY || fecha.getDayOfWeek() == DayOfWeek.MONDAY) {
            fecha = fecha.plusDays(1);
        }
        return fecha.toString();
    }
}
