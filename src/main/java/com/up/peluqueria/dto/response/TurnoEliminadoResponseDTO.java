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
@Schema(description = "Confirmación de eliminación de un turno")
public class TurnoEliminadoResponseDTO {

    @Schema(description = "Mensaje descriptivo", example = "Turno eliminado correctamente")
    private String message;

    @Schema(description = "Datos del turno eliminado")
    private TurnoResponseDTO turno;
}
