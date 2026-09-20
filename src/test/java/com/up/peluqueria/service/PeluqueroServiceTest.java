package com.up.peluqueria.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.up.peluqueria.dto.request.PeluqueroRequestDTO;
import com.up.peluqueria.dto.response.PeluqueroEliminadoResponseDTO;
import com.up.peluqueria.dto.response.PeluqueroResponseDTO;
import com.up.peluqueria.entity.Peluquero;
import com.up.peluqueria.exception.BadRequestException;
import com.up.peluqueria.exception.ConflictException;
import com.up.peluqueria.exception.ResourceNotFoundException;
import com.up.peluqueria.repository.PeluqueroRepository;
import com.up.peluqueria.repository.TurnoRepository;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class PeluqueroServiceTest {

    @Mock
    private PeluqueroRepository peluqueroRepository;

    @Mock
    private TurnoRepository turnoRepository;

    @InjectMocks
    private PeluqueroService peluqueroService;

    private Peluquero peluquero(Long id, String name) {
        return Peluquero.builder().id(id).name(name).build();
    }

    @Test
    @DisplayName("listarTodos devuelve todos los peluqueros mapeados a DTO")
    void listarTodos_devuelveLista() {
        when(peluqueroRepository.findAll()).thenReturn(List.of(peluquero(1L, "Mateo"), peluquero(2L, "Tobias")));

        List<PeluqueroResponseDTO> resultado = peluqueroService.listarTodos();

        assertThat(resultado).hasSize(2);
        assertThat(resultado.get(0).getName()).isEqualTo("Pedro");
    }

    @Test
    @DisplayName("buscarPorId devuelve el peluquero si existe")
    void buscarPorId_existente() {
        when(peluqueroRepository.findById(1L)).thenReturn(Optional.of(peluquero(1L, "Mateo")));

        PeluqueroResponseDTO resultado = peluqueroService.buscarPorId(1L);

        assertThat(resultado.getId()).isEqualTo(1L);
        assertThat(resultado.getName()).isEqualTo("Mateo");
    }

    @Test
    @DisplayName("buscarPorId lanza ResourceNotFoundException si no existe")
    void buscarPorId_inexistente() {
        when(peluqueroRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> peluqueroService.buscarPorId(99L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("99");
    }

    @Test
    @DisplayName("crear guarda y devuelve el peluquero si el nombre no existe")
    void crear_ok() {
        when(peluqueroRepository.findByName("Mateo")).thenReturn(Optional.empty());
        when(peluqueroRepository.save(any(Peluquero.class))).thenReturn(peluquero(1L, "Mateo"));

        PeluqueroResponseDTO resultado = peluqueroService.crear(new PeluqueroRequestDTO("Mateo"));

        assertThat(resultado.getId()).isEqualTo(1L);
        verify(peluqueroRepository).save(any(Peluquero.class));
    }

    @Test
    @DisplayName("crear lanza BadRequestException si el nombre ya existe")
    void crear_nombreDuplicado() {
        when(peluqueroRepository.findByName("Mateo")).thenReturn(Optional.of(peluquero(1L, "Mateo")));

        assertThatThrownBy(() -> peluqueroService.crear(new PeluqueroRequestDTO("Mateo")))
                .isInstanceOf(BadRequestException.class);
        verify(peluqueroRepository, never()).save(any());
    }

    @Test
    @DisplayName("eliminar borra el peluquero si no tiene turnos")
    void eliminar_sinTurnos() {
        when(peluqueroRepository.findById(1L)).thenReturn(Optional.of(peluquero(1L, "Mateo")));
        when(turnoRepository.countByPeluquero_Id(1L)).thenReturn(0L);

        PeluqueroEliminadoResponseDTO resultado = peluqueroService.eliminar(1L, false);

        assertThat(resultado.getPeluquero().getName()).isEqualTo("Mateo");
        verify(peluqueroRepository).deleteById(1L);
        verify(turnoRepository, never()).deleteByPeluquero_Id(any());
    }

    @Test
    @DisplayName("eliminar lanza ConflictException si tiene turnos y no se confirma")
    void eliminar_conTurnosSinConfirmar() {
        when(peluqueroRepository.findById(1L)).thenReturn(Optional.of(peluquero(1L, "Mateo")));
        when(turnoRepository.countByPeluquero_Id(1L)).thenReturn(2L);

        assertThatThrownBy(() -> peluqueroService.eliminar(1L, false))
                .isInstanceOf(ConflictException.class)
                .hasMessageContaining("2 turnos");
        verify(peluqueroRepository, never()).deleteById(any());
    }

    @Test
    @DisplayName("eliminar con confirmación borra los turnos y el peluquero")
    void eliminar_conTurnosConfirmado() {
        when(peluqueroRepository.findById(1L)).thenReturn(Optional.of(peluquero(1L, "Mateo")));
        when(turnoRepository.countByPeluquero_Id(1L)).thenReturn(2L);

        peluqueroService.eliminar(1L, true);

        verify(turnoRepository).deleteByPeluquero_Id(1L);
        verify(peluqueroRepository).deleteById(1L);
    }
}
