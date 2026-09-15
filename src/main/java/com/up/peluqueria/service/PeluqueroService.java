package com.up.peluqueria.service;

import com.up.peluqueria.dto.request.PeluqueroRequestDTO;
import com.up.peluqueria.dto.response.PeluqueroEliminadoResponseDTO;
import com.up.peluqueria.dto.response.PeluqueroResponseDTO;
import com.up.peluqueria.entity.Peluquero;
import com.up.peluqueria.exception.BadRequestException;
import com.up.peluqueria.exception.ConflictException;
import com.up.peluqueria.exception.ResourceNotFoundException;
import com.up.peluqueria.repository.PeluqueroRepository;
import com.up.peluqueria.repository.TurnoRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class PeluqueroService {

    @Autowired
    private PeluqueroRepository peluqueroRepository;

    @Autowired
    private TurnoRepository turnoRepository;

    @Transactional(readOnly = true)
    public List<PeluqueroResponseDTO> listarTodos() {
        return peluqueroRepository.findAll()
                .stream()
                .map(this::convertirEntidadADtoResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public PeluqueroResponseDTO buscarPorId(Long id) {
        log.info("Buscando peluquero por ID: {}", id);
        Peluquero peluquero = obtenerPeluqueroOrThrow(id);
        return convertirEntidadADtoResponse(peluquero);
    }

    @Transactional
    public PeluqueroResponseDTO crear(PeluqueroRequestDTO peluqueroRequestDTO) {
        if (peluqueroRepository.findByName(peluqueroRequestDTO.getName()).isPresent()) {
            throw new BadRequestException(
                    "Ya existe un peluquero con el nombre '" + peluqueroRequestDTO.getName() + "'");
        }

        Peluquero peluquero = Peluquero.builder()
                .name(peluqueroRequestDTO.getName())
                .build();

        Peluquero peluqueroCreado = peluqueroRepository.save(peluquero);
        return convertirEntidadADtoResponse(peluqueroCreado);
    }

    @Transactional
    public PeluqueroResponseDTO actualizar(Long id, PeluqueroRequestDTO peluqueroRequestDTO) {
        Peluquero peluqueroExistente = obtenerPeluqueroOrThrow(id);

        peluqueroExistente.setName(peluqueroRequestDTO.getName());

        Peluquero peluqueroActualizado = peluqueroRepository.save(peluqueroExistente);
        return convertirEntidadADtoResponse(peluqueroActualizado);
    }

    @Transactional
    public PeluqueroEliminadoResponseDTO eliminar(Long id, boolean confirmar) {
        Peluquero peluquero = obtenerPeluqueroOrThrow(id);

        long cantidadTurnos = turnoRepository.countByPeluquero_Id(id);

        if (cantidadTurnos > 0 && !confirmar) {
            throw new ConflictException(
                    "El peluquero tiene " + cantidadTurnos + " turno" + (cantidadTurnos > 1 ? "s" : "")
                            + " asociado" + (cantidadTurnos > 1 ? "s" : "") + ". "
                            + "¿Desea eliminarlo junto con sus turnos?");
        }

        if (cantidadTurnos > 0) {
            turnoRepository.deleteByPeluquero_Id(id);
        }

        PeluqueroResponseDTO peluqueroEliminado = convertirEntidadADtoResponse(peluquero);
        peluqueroRepository.deleteById(id);

        return PeluqueroEliminadoResponseDTO.builder()
                .message("Peluquero eliminado correctamente")
                .peluquero(peluqueroEliminado)
                .build();
    }

    private Peluquero obtenerPeluqueroOrThrow(Long id) {
        return peluqueroRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Peluquero con id " + id + " no encontrado"));
    }

    private PeluqueroResponseDTO convertirEntidadADtoResponse(Peluquero peluquero) {
        return PeluqueroResponseDTO.builder()
                .id(peluquero.getId())
                .name(peluquero.getName())
                .build();
    }
}
