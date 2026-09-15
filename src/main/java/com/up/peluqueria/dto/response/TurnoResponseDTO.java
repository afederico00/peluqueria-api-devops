package com.up.peluqueria.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Datos de un turno, incluyendo el peluquero asignado")
public class TurnoResponseDTO {

    @Schema(description = "Identificador único del turno", example = "1")
    private Long id;

    @Schema(description = "Fecha del turno en formato YYYY-MM-DD", example = "2025-06-17")
    private String fecha;

    @Schema(description = "Hora del turno en formato HH:MM", example = "10:30")
    private String hora;

    @Schema(description = "Estado del turno", example = "RESERVADO", allowableValues = { "RESERVADO", "FINALIZADO" })
    private String estadoTurno;

    @Schema(description = "ID del peluquero asignado al turno", example = "1")
    private Long peluqueroId;

    @Schema(description = "Datos del peluquero asignado")
    private PeluqueroResponseDTO peluquero;
}
