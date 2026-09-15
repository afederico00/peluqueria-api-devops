package com.up.peluqueria.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Datos para crear o actualizar un turno")
public class TurnoRequestDTO {

    @NotBlank(message = "La fecha es requerida")
    @Schema(description = "Fecha del turno en formato YYYY-MM-DD. Solo se aceptan turnos de martes a sábado.",
            example = "2025-06-17")
    private String fecha;

    @NotBlank(message = "La hora es requerida")
    @Schema(description = "Hora del turno en formato HH:MM, entre 09:00 y 17:30, en punto o y media (ej: 10:00, 10:30).",
            example = "10:30")
    private String hora;

    @NotBlank(message = "El estado del turno es requerido")
    @Schema(description = "Estado del turno. RESERVADO: horario ocupado/pendiente. FINALIZADO: turno ya realizado.",
            example = "RESERVADO", allowableValues = { "RESERVADO", "FINALIZADO" })
    private String estadoTurno;

    @NotNull(message = "El id del peluquero es requerido")
    @Schema(description = "ID del peluquero al que se asigna el turno", example = "1")
    private Long peluqueroId;
}
