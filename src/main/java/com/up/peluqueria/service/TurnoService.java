package com.up.peluqueria.service;

import com.up.peluqueria.dto.request.TurnoRequestDTO;
import com.up.peluqueria.dto.response.PeluqueroResponseDTO;
import com.up.peluqueria.dto.response.TurnoEliminadoResponseDTO;
import com.up.peluqueria.dto.response.TurnoResponseDTO;
import com.up.peluqueria.entity.EstadoTurno;
import com.up.peluqueria.entity.Peluquero;
import com.up.peluqueria.entity.Turno;
import com.up.peluqueria.exception.BadRequestException;
import com.up.peluqueria.exception.ResourceNotFoundException;
import com.up.peluqueria.repository.PeluqueroRepository;
import com.up.peluqueria.repository.TurnoRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
public class TurnoService {

    private static final int HORA_APERTURA_MIN = 9 * 60;       // 09:00
    private static final int HORA_CIERRE_MIN = 17 * 60 + 30;   // 17:30

    @Autowired
    private TurnoRepository turnoRepository;

    @Autowired
    private PeluqueroRepository peluqueroRepository;

    @Transactional(readOnly = true)
    public List<TurnoResponseDTO> listarTodos() {
        return turnoRepository.findAllByOrderByFechaAscHoraAsc()
                .stream()
                .map(this::convertirEntidadADtoResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public TurnoResponseDTO buscarPorId(Long id) {
        log.info("Buscando turno por ID: {}", id);
        Turno turno = obtenerTurnoOrThrow(id);
        return convertirEntidadADtoResponse(turno);
    }

    @Transactional
    public TurnoResponseDTO crear(TurnoRequestDTO turnoRequestDTO) {
        EstadoTurno estadoTurno = validarDatosTurno(turnoRequestDTO);

        Peluquero peluquero = peluqueroRepository.findById(turnoRequestDTO.getPeluqueroId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Peluquero con id " + turnoRequestDTO.getPeluqueroId() + " no encontrado"));

        if (estadoTurno == EstadoTurno.RESERVADO) {
            validarSinConflictoDeHorario(turnoRequestDTO, null);
        }

        Turno turno = Turno.builder()
                .fecha(turnoRequestDTO.getFecha())
                .hora(turnoRequestDTO.getHora())
                .estadoTurno(estadoTurno)
                .peluquero(peluquero)
                .build();

        Turno turnoCreado = turnoRepository.save(turno);
        return convertirEntidadADtoResponse(turnoCreado);
    }

    @Transactional
    public TurnoResponseDTO actualizar(Long id, TurnoRequestDTO turnoRequestDTO) {
        EstadoTurno estadoTurno = validarDatosTurno(turnoRequestDTO);

        Turno turnoExistente = obtenerTurnoOrThrow(id);

        Peluquero peluquero = peluqueroRepository.findById(turnoRequestDTO.getPeluqueroId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Peluquero con id " + turnoRequestDTO.getPeluqueroId() + " no encontrado"));

        if (estadoTurno == EstadoTurno.RESERVADO) {
            validarSinConflictoDeHorario(turnoRequestDTO, id);
        }

        turnoExistente.setFecha(turnoRequestDTO.getFecha());
        turnoExistente.setHora(turnoRequestDTO.getHora());
        turnoExistente.setEstadoTurno(estadoTurno);
        turnoExistente.setPeluquero(peluquero);

        Turno turnoActualizado = turnoRepository.save(turnoExistente);
        return convertirEntidadADtoResponse(turnoActualizado);
    }

    @Transactional
    public TurnoEliminadoResponseDTO eliminar(Long id) {
        Turno turno = obtenerTurnoOrThrow(id);
        TurnoResponseDTO turnoEliminado = convertirEntidadADtoResponse(turno);

        turnoRepository.deleteById(id);

        return TurnoEliminadoResponseDTO.builder()
                .message("Turno eliminado correctamente")
                .turno(turnoEliminado)
                .build();
    }

    private void validarSinConflictoDeHorario(TurnoRequestDTO turnoRequestDTO, Long idTurnoActual) {
        Optional<Turno> reservado = turnoRepository.findByPeluquero_IdAndFechaAndHoraAndEstadoTurno(
                turnoRequestDTO.getPeluqueroId(),
                turnoRequestDTO.getFecha(),
                turnoRequestDTO.getHora(),
                EstadoTurno.RESERVADO);

        if (reservado.isPresent() && (idTurnoActual == null || !reservado.get().getId().equals(idTurnoActual))) {
            throw new BadRequestException(
                    "El peluquero ya tiene un turno RESERVADO el " + turnoRequestDTO.getFecha()
                            + " a las " + turnoRequestDTO.getHora());
        }
    }

    /**
     * Replica las validaciones de negocio que la aplicación Node original
     * realiza en CreateUpdateTurnoDto.create(): formato y validez de fecha,
     * día de la semana permitido, formato de hora, incrementos de 30 minutos,
     * rango horario de atención y validez del estado del turno.
     */
    private EstadoTurno validarDatosTurno(TurnoRequestDTO dto) {
        LocalDate fecha;
        try {
            fecha = LocalDate.parse(dto.getFecha());
        } catch (DateTimeParseException e) {
            throw new BadRequestException("La fecha debe ser una fecha válida (YYYY-MM-DD)");
        }

        DayOfWeek diaSemana = fecha.getDayOfWeek();
        if (diaSemana == DayOfWeek.SUNDAY || diaSemana == DayOfWeek.MONDAY) {
            throw new BadRequestException("Solo se pueden reservar turnos de martes a sábado");
        }

        if (dto.getHora() == null || !dto.getHora().matches("\\d{2}:\\d{2}")) {
            throw new BadRequestException("La hora debe tener el formato HH:MM");
        }

        String[] partesHora = dto.getHora().split(":");
        int hh = Integer.parseInt(partesHora[0]);
        int mm = Integer.parseInt(partesHora[1]);

        if (mm != 0 && mm != 30) {
            throw new BadRequestException(
                    "Los turnos solo pueden reservarse en punto o y media (ej: 12:00 o 12:30)");
        }

        int totalMin = hh * 60 + mm;
        if (totalMin < HORA_APERTURA_MIN || totalMin > HORA_CIERRE_MIN) {
            throw new BadRequestException("Los turnos solo pueden reservarse de 09:00 a 17:30");
        }

        boolean estadoValido = Arrays.stream(EstadoTurno.values())
                .anyMatch(estado -> estado.name().equals(dto.getEstadoTurno()));
        if (!estadoValido) {
            String estadosValidos = Arrays.stream(EstadoTurno.values())
                    .map(Enum::name)
                    .collect(Collectors.joining(", "));
            throw new BadRequestException("El estadoTurno debe ser uno de: " + estadosValidos);
        }

        if (dto.getPeluqueroId() <= 0) {
            throw new BadRequestException("El id del peluquero debe ser un número válido");
        }

        return EstadoTurno.valueOf(dto.getEstadoTurno());
    }

    private Turno obtenerTurnoOrThrow(Long id) {
        return turnoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Turno con id " + id + " no encontrado"));
    }

    private TurnoResponseDTO convertirEntidadADtoResponse(Turno turno) {
        Peluquero peluquero = turno.getPeluquero();

        return TurnoResponseDTO.builder()
                .id(turno.getId())
                .fecha(turno.getFecha())
                .hora(turno.getHora())
                .estadoTurno(turno.getEstadoTurno().name())
                .peluqueroId(peluquero.getId())
                .peluquero(PeluqueroResponseDTO.builder()
                        .id(peluquero.getId())
                        .name(peluquero.getName())
                        .build())
                .build();
    }
}
