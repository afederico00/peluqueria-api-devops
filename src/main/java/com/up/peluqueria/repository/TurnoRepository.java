package com.up.peluqueria.repository;

import com.up.peluqueria.entity.EstadoTurno;
import com.up.peluqueria.entity.Turno;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TurnoRepository extends JpaRepository<Turno, Long> {

    /**
     * Trae el peluquero junto con el turno en la misma consulta (JOIN FETCH),
     * evitando el problema N+1 al mapear a TurnoResponseDTO. Equivale al
     * `include: { peluquero: true }` de la aplicación Node original.
     */
    @Query("SELECT t FROM Turno t JOIN FETCH t.peluquero ORDER BY t.fecha ASC, t.hora ASC")
    List<Turno> findAllByOrderByFechaAscHoraAsc();

    Optional<Turno> findByPeluquero_IdAndFechaAndHoraAndEstadoTurno(
            Long peluqueroId, String fecha, String hora, EstadoTurno estadoTurno);

    long countByPeluquero_Id(Long peluqueroId);

    long deleteByPeluquero_Id(Long peluqueroId);
}
