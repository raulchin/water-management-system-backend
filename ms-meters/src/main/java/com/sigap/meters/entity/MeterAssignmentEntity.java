package com.sigap.meters.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "medidor_socios")
public class MeterAssignmentEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "asignacion_id")
    private Long asignacionId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "medidor_id", nullable = false)
    private MeterEntity medidor;

    @Column(name = "socio_id", nullable = false)
    private Long socioId;

    @Column(name = "fecha_asignacion", nullable = false)
    private LocalDate fechaAsignacion;

    @Column(name = "fecha_retiro")
    private LocalDate fechaRetiro;

    @Column(name = "estado", nullable = false, length = 20)
    private String estado;

    @Column(name = "observacion", length = 500)
    private String observacion;

    @Column(name = "fecha_actualizacion")
    private LocalDateTime fechaActualizacion;

    @PrePersist
    public void prePersist() {
        if (fechaAsignacion == null) {
            fechaAsignacion = LocalDate.now();
        }

        if (estado == null || estado.isBlank()) {
            estado = "ACTIVO";
        }
    }

    @PreUpdate
    public void preUpdate() {
        fechaActualizacion = LocalDateTime.now();
    }
}
