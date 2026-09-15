package com.up.peluqueria.mcp;

import com.up.peluqueria.dto.request.TurnoRequestDTO;
import com.up.peluqueria.dto.response.TurnoEliminadoResponseDTO;
import com.up.peluqueria.dto.response.TurnoResponseDTO;
import com.up.peluqueria.service.TurnoService;
import org.springframework.ai.mcp.annotation.McpTool;
import org.springframework.ai.mcp.annotation.McpToolParam;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class TurnoMcpTools {

    private final TurnoService turnoService;

    public TurnoMcpTools(TurnoService turnoService) {
        this.turnoService = turnoService;
    }

    @McpTool(description = "Lista todos los turnos registrados en la aplicación.")
    public List<TurnoResponseDTO> listar_turnos() {
        return turnoService.listarTodos();
    }

    @McpTool(description = "Busca un turno por su ID.")
    public TurnoResponseDTO buscar_turno(
            @McpToolParam(description = "ID del turno a buscar.", required = true) Long id) {
        return turnoService.buscarPorId(id);
    }

    @McpTool(description = "Crea un turno para un peluquero. Solo se aceptan turnos de martes a sábado, entre 09:00 y 17:30, en punto o y media, y no puede haber dos turnos RESERVADO para el mismo peluquero, fecha y hora.")
    public TurnoResponseDTO crear_turno(
            @McpToolParam(description = "Fecha del turno en formato YYYY-MM-DD.", required = true) String fecha,
            @McpToolParam(description = "Hora del turno en formato HH:MM.", required = true) String hora,
            @McpToolParam(description = "Estado del turno, por ejemplo RESERVADO o FINALIZADO.", required = true) String estadoTurno,
            @McpToolParam(description = "ID del peluquero al que se asigna el turno.", required = true) Long peluqueroId) {
        TurnoRequestDTO dto = new TurnoRequestDTO(fecha, hora, estadoTurno, peluqueroId);
        return turnoService.crear(dto);
    }

    @McpTool(description = "Actualiza un turno existente. Solo se aceptan turnos de martes a sábado, entre 09:00 y 17:30, en punto o y media, y no puede haber dos turnos RESERVADO para el mismo peluquero, fecha y hora.")
    public TurnoResponseDTO actualizar_turno(
            @McpToolParam(description = "ID del turno a actualizar.", required = true) Long id,
            @McpToolParam(description = "Fecha del turno en formato YYYY-MM-DD.", required = true) String fecha,
            @McpToolParam(description = "Hora del turno en formato HH:MM.", required = true) String hora,
            @McpToolParam(description = "Estado del turno, por ejemplo RESERVADO o FINALIZADO.", required = true) String estadoTurno,
            @McpToolParam(description = "ID del peluquero al que se asigna el turno.", required = true) Long peluqueroId) {
        TurnoRequestDTO dto = new TurnoRequestDTO(fecha, hora, estadoTurno, peluqueroId);
        return turnoService.actualizar(id, dto);
    }

    @McpTool(description = "Elimina un turno por su ID.")
    public TurnoEliminadoResponseDTO eliminar_turno(
            @McpToolParam(description = "ID del turno a eliminar.", required = true) Long id) {
        return turnoService.eliminar(id);
    }
}
