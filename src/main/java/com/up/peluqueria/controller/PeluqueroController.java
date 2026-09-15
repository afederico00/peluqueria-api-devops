package com.up.peluqueria.controller;

import com.up.peluqueria.dto.request.PeluqueroRequestDTO;
import com.up.peluqueria.dto.response.PeluqueroEliminadoResponseDTO;
import com.up.peluqueria.dto.response.PeluqueroResponseDTO;
import com.up.peluqueria.exception.ErrorResponse;
import com.up.peluqueria.service.PeluqueroService;
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
@RequestMapping("/api/peluqueros")
@Tag(name = "Peluqueros", description = "Operaciones sobre peluqueros")
@Slf4j
public class PeluqueroController {

    @Autowired
    private PeluqueroService peluqueroService;

    @GetMapping
    @Operation(summary = "Obtener todos los peluqueros",
            description = "Retorna la lista completa de peluqueros registrados en el sistema.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lista de peluqueros obtenida exitosamente",
                    content = @Content(schema = @Schema(implementation = PeluqueroResponseDTO.class))),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<List<PeluqueroResponseDTO>> listarTodos() {
        log.info("Recibida la peticion para listar todos los peluqueros");
        return ResponseEntity.ok(peluqueroService.listarTodos());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener un peluquero por ID",
            description = "Retorna los datos de un peluquero específico identificado por su ID.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Peluquero encontrado",
                    content = @Content(schema = @Schema(implementation = PeluqueroResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "Peluquero no encontrado",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(value = "{\"message\": \"Peluquero con id 99 no encontrado\"}"))),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<PeluqueroResponseDTO> buscarPorId(
            @Parameter(description = "ID numérico del peluquero", example = "1") @PathVariable Long id) {
        log.info("Recibida la peticion para buscar peluquero con ID: {}", id);
        return ResponseEntity.ok(peluqueroService.buscarPorId(id));
    }

    @PostMapping
    @Operation(summary = "Crear un nuevo peluquero",
            description = "Crea un nuevo peluquero en el sistema. El nombre debe ser único; " +
                    "si ya existe un peluquero con ese nombre se retorna un error 400.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Peluquero creado exitosamente",
                    content = @Content(schema = @Schema(implementation = PeluqueroResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Datos inválidos o nombre duplicado",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class),
                            examples = {
                                    @ExampleObject(name = "nombreRequerido",
                                            value = "{\"message\": \"El nombre es requerido\"}"),
                                    @ExampleObject(name = "nombreDuplicado",
                                            value = "{\"message\": \"Ya existe un peluquero con el nombre 'Carlos'\"}")
                            })),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<PeluqueroResponseDTO> crear(
            @Valid @RequestBody PeluqueroRequestDTO peluqueroRequestDTO) {
        log.info("Recibida la peticion para registrar peluquero con nombre: {}", peluqueroRequestDTO.getName());
        PeluqueroResponseDTO peluqueroCreado = peluqueroService.crear(peluqueroRequestDTO);
        return new ResponseEntity<>(peluqueroCreado, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar un peluquero",
            description = "Actualiza los datos de un peluquero existente identificado por su ID.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Peluquero actualizado exitosamente",
                    content = @Content(schema = @Schema(implementation = PeluqueroResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Datos inválidos en el cuerpo de la solicitud",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Peluquero no encontrado",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<PeluqueroResponseDTO> actualizar(
            @Parameter(description = "ID numérico del peluquero", example = "1") @PathVariable Long id,
            @Valid @RequestBody PeluqueroRequestDTO peluqueroRequestDTO) {
        log.info("Recibida la peticion para actualizar peluquero con ID: {}", id);
        return ResponseEntity.ok(peluqueroService.actualizar(id, peluqueroRequestDTO));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar un peluquero",
            description = "Elimina permanentemente un peluquero identificado por su ID. " +
                    "Si el peluquero tiene turnos asociados y no se envía `confirmar=true`, " +
                    "se retorna un error 409 solicitando confirmación. Si se confirma, se eliminan " +
                    "también los turnos asociados.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Peluquero eliminado exitosamente",
                    content = @Content(schema = @Schema(implementation = PeluqueroEliminadoResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "Peluquero no encontrado",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "409", description = "El peluquero tiene turnos asociados y no se confirmó la eliminación",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<PeluqueroEliminadoResponseDTO> eliminar(
            @Parameter(description = "ID numérico del peluquero", example = "1") @PathVariable Long id,
            @Parameter(description = "Confirma la eliminación en cascada de los turnos asociados")
            @RequestParam(defaultValue = "false") boolean confirmar) {
        log.info("Recibida la peticion para eliminar peluquero con ID: {} (confirmar={})", id, confirmar);
        return ResponseEntity.ok(peluqueroService.eliminar(id, confirmar));
    }
}
