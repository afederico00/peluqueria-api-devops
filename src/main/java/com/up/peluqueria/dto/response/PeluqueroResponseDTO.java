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
@Schema(description = "Datos de un peluquero")
public class PeluqueroResponseDTO {

    @Schema(description = "Identificador único del peluquero", example = "1")
    private Long id;

    @Schema(description = "Nombre del peluquero", example = "Tobias")
    private String name;
}
