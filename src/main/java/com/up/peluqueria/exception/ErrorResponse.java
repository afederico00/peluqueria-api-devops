package com.up.peluqueria.exception;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Estructura estándar de error devuelta por la API")
public class ErrorResponse {

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    @Schema(description = "Momento en que ocurrió el error")
    private LocalDateTime timestamp;

    @Schema(description = "Código de estado HTTP", example = "404")
    private Integer status;

    @Schema(description = "Nombre del error HTTP", example = "Not Found")
    private String error;

    @Schema(description = "Mensaje descriptivo del error", example = "Peluquero con id 99 no encontrado")
    private String message;

    @Schema(description = "Path del recurso solicitado", example = "/api/peluqueros/99")
    private String path;

    @Schema(description = "Detalle de errores de validación por campo, si corresponde")
    private List<String> erroresValidacion;
}
