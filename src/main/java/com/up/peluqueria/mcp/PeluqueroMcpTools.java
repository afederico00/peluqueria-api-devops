package com.up.peluqueria.mcp;

import com.up.peluqueria.dto.request.PeluqueroRequestDTO;
import com.up.peluqueria.dto.response.PeluqueroEliminadoResponseDTO;
import com.up.peluqueria.dto.response.PeluqueroResponseDTO;
import com.up.peluqueria.service.PeluqueroService;
import org.springframework.ai.mcp.annotation.McpTool;
import org.springframework.ai.mcp.annotation.McpToolParam;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class PeluqueroMcpTools {

    private final PeluqueroService peluqueroService;

    public PeluqueroMcpTools(PeluqueroService peluqueroService) {
        this.peluqueroService = peluqueroService;
    }

    @McpTool(description = "Lista todos los peluqueros registrados en la aplicación.")
    public List<PeluqueroResponseDTO> listar_peluqueros() {
        return peluqueroService.listarTodos();
    }

    @McpTool(description = "Busca un peluquero por su ID.")
    public PeluqueroResponseDTO buscar_peluquero(
            @McpToolParam(description = "ID del peluquero a buscar.", required = true) Long id) {
        return peluqueroService.buscarPorId(id);
    }

    @McpTool(description = "Crea un nuevo peluquero con el nombre indicado.")
    public PeluqueroResponseDTO crear_peluquero(
            @McpToolParam(description = "Nombre del peluquero a crear.", required = true) String name) {
        PeluqueroRequestDTO dto = new PeluqueroRequestDTO(name);
        return peluqueroService.crear(dto);
    }

    @McpTool(description = "Actualiza el nombre de un peluquero existente.")
    public PeluqueroResponseDTO actualizar_peluquero(
            @McpToolParam(description = "ID del peluquero a actualizar.", required = true) Long id,
            @McpToolParam(description = "Nuevo nombre del peluquero.", required = true) String name) {
        PeluqueroRequestDTO dto = new PeluqueroRequestDTO(name);
        return peluqueroService.actualizar(id, dto);
    }

    @McpTool(description = "Elimina un peluquero por ID. Si tiene turnos asociados, debe indicarse confirmar=true para aceptarlo.")
    public PeluqueroEliminadoResponseDTO eliminar_peluquero(
            @McpToolParam(description = "ID del peluquero a eliminar.", required = true) Long id,
            @McpToolParam(description = "Confirma la eliminación cuando el peluquero tiene turnos asociados.", required = false) Boolean confirmar) {
        return peluqueroService.eliminar(id, Boolean.TRUE.equals(confirmar));
    }
}
