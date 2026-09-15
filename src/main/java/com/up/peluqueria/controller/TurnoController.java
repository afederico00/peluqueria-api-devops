package com.up.peluqueria.controller;

import com.up.peluqueria.dto.request.TurnoRequestDTO;
import com.up.peluqueria.dto.response.TurnoEliminadoResponseDTO;
import com.up.peluqueria.dto.response.TurnoResponseDTO;
import com.up.peluqueria.exception.ErrorResponse;
import com.up.peluqueria.service.TurnoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/turnos")
@Tag(name = "Turnos", description = "Operaciones sobre turnos. Un turno en estado RESERVADO indica un " +
        "horario ocupado; FINALIZADO indica que el turno ya fue realizado.")
@Slf4j
public class TurnoController {

    @Autowired
    private TurnoService turnoService;

    @GetMapping
    @Operation(summary = "Obtener todos los turnos",
            description = "Retorna la lista completa de turnos registrados, incluyendo los datos " +
                    "del peluquero asignado a cada uno.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lista de turnos obtenida exitosamente",
                    content = @Content(schema = @Schema(implementation = TurnoResponseDTO.class))),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<List<TurnoResponseDTO>> listarTodos() {
        log.info("Recibida la peticion para listar todos los turnos");
        return ResponseEntity.ok(turnoService.listarTodos());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener un turno por ID",
            description = "Retorna los datos de un turno específico identificado por su ID, " +
                    "incluyendo los datos del peluquero asignado.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Turno encontrado",
                    content = @Content(schema = @Schema(implementation = TurnoResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "Turno no encontrado",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<TurnoResponseDTO> buscarPorId(
            @Parameter(description = "ID numérico del turno", example = "1") @PathVariable Long id) {
        log.info("Recibida la peticion para buscar turno con ID: {}", id);
        return ResponseEntity.ok(turnoService.buscarPorId(id));
    }

    @PostMapping
    @Operation(summary = "Crear un nuevo turno",
            description = "Crea un nuevo turno y lo asigna a un peluquero. " +
                    "Solo se pueden reservar turnos de **martes a sábado**, en horario de **09:00 a 17:30**, " +
                    "únicamente en punto o y media (ej: 10:00, 10:30). " +
                    "Si el estado es **RESERVADO**, no puede existir otro turno RESERVADO para el mismo " +
                    "peluquero, fecha y hora (retorna 400). Si el peluquero indicado no existe, retorna 404.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Turno creado exitosamente",
                    content = @Content(schema = @Schema(implementation = TurnoResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Datos inválidos o conflicto de reserva",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class),
                            examples = {
                                    @ExampleObject(name = "fechaInvalida",
                                            value = "{\"message\": \"La fecha debe ser una fecha válida (YYYY-MM-DD)\"}"),
                                    @ExampleObject(name = "horaInvalida",
                                            value = "{\"message\": \"La hora debe tener el formato HH:MM\"}"),
                                    @ExampleObject(name = "diaSemana",
                                            value = "{\"message\": \"Solo se pueden reservar turnos de martes a sábado\"}"),
                                    @ExampleObject(name = "minutosInvalidos",
                                            value = "{\"message\": \"Los turnos solo pueden reservarse en punto o y media (ej: 12:00 o 12:30)\"}"),
                                    @ExampleObject(name = "rangoHorario",
                                            value = "{\"message\": \"Los turnos solo pueden reservarse de 09:00 a 17:30\"}"),
                                    @ExampleObject(name = "estadoInvalido",
                                            value = "{\"message\": \"El estadoTurno debe ser uno de: RESERVADO, FINALIZADO\"}"),
                                    @ExampleObject(name = "turnoOcupado",
                                            value = "{\"message\": \"El peluquero ya tiene un turno RESERVADO el 2025-06-17 a las 10:30\"}")
                            })),
            @ApiResponse(responseCode = "404", description = "Peluquero no encontrado",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<TurnoResponseDTO> crear(@Valid @RequestBody TurnoRequestDTO turnoRequestDTO) {
        log.info("Recibida la peticion para registrar turno para el peluquero con ID: {}",
                turnoRequestDTO.getPeluqueroId());
        TurnoResponseDTO turnoCreado = turnoService.crear(turnoRequestDTO);
        return new ResponseEntity<>(turnoCreado, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar un turno",
            description = "Actualiza los datos de un turno existente. Se aplican las mismas restricciones " +
                    "de fecha y hora que al crear: solo **martes a sábado**, de **09:00 a 17:30**, en punto " +
                    "o y media. Si el nuevo estado es **RESERVADO**, valida que no exista conflicto de " +
                    "horario con otro turno del mismo peluquero (excluyendo el turno actual). Si el " +
                    "peluquero indicado no existe, retorna 404.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Turno actualizado exitosamente",
                    content = @Content(schema = @Schema(implementation = TurnoResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Datos inválidos o conflicto de reserva",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Turno o peluquero no encontrado",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class),
                            examples = {
                                    @ExampleObject(name = "turnoNoEncontrado",
                                            value = "{\"message\": \"Turno con id 99 no encontrado\"}"),
                                    @ExampleObject(name = "peluqueroNoEncontrado",
                                            value = "{\"message\": \"Peluquero con id 99 no encontrado\"}")
                            })),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<TurnoResponseDTO> actualizar(
            @Parameter(description = "ID numérico del turno", example = "1") @PathVariable Long id,
            @Valid @RequestBody TurnoRequestDTO turnoRequestDTO) {
        log.info("Recibida la peticion para actualizar turno con ID: {}", id);
        return ResponseEntity.ok(turnoService.actualizar(id, turnoRequestDTO));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar un turno", description = "Elimina permanentemente un turno identificado por su ID.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Turno eliminado exitosamente",
                    content = @Content(schema = @Schema(implementation = TurnoEliminadoResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "Turno no encontrado",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<TurnoEliminadoResponseDTO> eliminar(
            @Parameter(description = "ID numérico del turno", example = "1") @PathVariable Long id) {
        log.info("Recibida la peticion para eliminar turno con ID: {}", id);
        return ResponseEntity.ok(turnoService.eliminar(id));
    }
}
