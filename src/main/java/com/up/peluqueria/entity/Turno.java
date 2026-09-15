package com.up.peluqueria.entity;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "turnos")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonPropertyOrder({ "id", "fecha", "hora", "estadoTurno", "peluquero" })
public class Turno {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Se mantiene como String (formato YYYY-MM-DD) para conservar el mismo
     * criterio de validación manual que la aplicación Node original.
     */
    @Column(nullable = false)
    private String fecha;

    /**
     * Formato HH:MM.
     */
    @Column(nullable = false)
    private String hora;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoTurno estadoTurno;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "peluquero_id", nullable = false)
    private Peluquero peluquero;
}
