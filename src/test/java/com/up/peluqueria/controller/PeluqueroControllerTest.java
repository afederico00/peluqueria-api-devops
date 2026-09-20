package com.up.peluqueria.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.up.peluqueria.dto.response.PeluqueroResponseDTO;
import com.up.peluqueria.exception.ConflictException;
import com.up.peluqueria.exception.GlobalExceptionHandler;
import com.up.peluqueria.exception.ResourceNotFoundException;
import com.up.peluqueria.service.PeluqueroService;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

@ExtendWith(MockitoExtension.class)
class PeluqueroControllerTest {

    @Mock
    private PeluqueroService peluqueroService;

    @InjectMocks
    private PeluqueroController peluqueroController;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(peluqueroController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    @DisplayName("GET /api/peluqueros responde 200 con la lista")
    void listarTodos_200() throws Exception {
        when(peluqueroService.listarTodos()).thenReturn(List.of(new PeluqueroResponseDTO(1L, "Mateo")));

        mockMvc.perform(get("/api/peluqueros"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Mateo"));
    }

    @Test
    @DisplayName("GET /api/peluqueros/{id} responde 404 si no existe")
    void buscarPorId_404() throws Exception {
        when(peluqueroService.buscarPorId(99L))
                .thenThrow(new ResourceNotFoundException("Peluquero con id 99 no encontrado"));

        mockMvc.perform(get("/api/peluqueros/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Peluquero con id 99 no encontrado"));
    }

    @Test
    @DisplayName("POST /api/peluqueros responde 201 con datos válidos")
    void crear_201() throws Exception {
        when(peluqueroService.crear(any())).thenReturn(new PeluqueroResponseDTO(1L, "Mateo"));

        mockMvc.perform(post("/api/peluqueros")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\": \"Mateo\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    @DisplayName("POST /api/peluqueros responde 400 si el nombre está vacío")
    void crear_400_validacion() throws Exception {
        mockMvc.perform(post("/api/peluqueros")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\": \"\"}"))
                .andExpect(status().isBadRequest());

        verify(peluqueroService, never()).crear(any());
    }

    @Test
    @DisplayName("DELETE /api/peluqueros/{id} responde 409 si tiene turnos sin confirmar")
    void eliminar_409() throws Exception {
        when(peluqueroService.eliminar(1L, false))
                .thenThrow(new ConflictException("El peluquero tiene 2 turnos asociados."));

        mockMvc.perform(delete("/api/peluqueros/1")).andExpect(status().isConflict());
    }
}
