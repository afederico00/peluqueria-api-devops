package com.up.peluqueria.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.context.annotation.Configuration;

@OpenAPIDefinition(
        info =
                @Info(
                        title = "App Peluquería API",
                        version = "1.0.0",
                        description = "API REST para gestión de peluqueros y reservas de turnos de peluquería. "
                                + "Permite crear y administrar peluqueros, y gestionar sus turnos con estados "
                                + "RESERVADO y FINALIZADO."))
@Configuration
public class OpenApiConfig {}
