package com.up.peluqueria;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class PeluqueriaApplication {

	public static void main(String[] args) {
		SpringApplication.run(PeluqueriaApplication.class, args);
		System.out.println("\n" +
				"═══════════════════════════════════════════════\n" +
				" 🚀 PELUQUERIA SERVICE INICIADO CORRECTAMENTE\n" +
				"═══════════════════════════════════════════════\n" +
				" 📍 URL: http://localhost:8080/api\n" +
				" 📊 Swagger UI: http://localhost:8080/api-docs\n" +
				" ✅ Estado: ACTIVO\n" +
				"═══════════════════════════════════════════════\n");
	}

}
