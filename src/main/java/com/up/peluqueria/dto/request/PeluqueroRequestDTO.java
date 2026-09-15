package com.up.peluqueria.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Datos para crear o actualizar un peluquero")
public class PeluqueroRequestDTO {

    @NotBlank(message = "El nombre es requerido")
    @Schema(description = "Nombre del peluquero. Debe ser único.", example = "Mateo")
    private String name;
}
