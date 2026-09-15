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
@Schema(description = "Confirmación de eliminación de un peluquero")
public class PeluqueroEliminadoResponseDTO {

    @Schema(description = "Mensaje descriptivo", example = "Peluquero eliminado correctamente")
    private String message;

    @Schema(description = "Datos del peluquero eliminado")
    private PeluqueroResponseDTO peluquero;
}
