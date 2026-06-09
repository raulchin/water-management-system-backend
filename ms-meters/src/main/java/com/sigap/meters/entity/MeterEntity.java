package com.sigap.meters.entity;

import com.sigap.meters.enums.MeterStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "medidores")
public class MeterEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "medidor_id")
    private Long medidorId;

    @Column(name = "numero_medidor", nullable = false, unique = true, length = 50)
    private String numeroMedidor;

    @Column(name = "marca", length = 100)
    private String marca;

    @Column(name = "modelo", length = 100)
    private String modelo;

    @Column(name = "ubicacion", length = 255)
    private String ubicacion;

    @Column(name = "direccion_referencia", length = 255)
    private String direccionReferencia;

    @Column(name = "fecha_instalacion")
    private LocalDate fechaInstalacion;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado", nullable = false, length = 20)
    private MeterStatus estado = MeterStatus.ACTIVO;

    @Column(name = "observacion", length = 500)
    private String observacion;

    @Column(name = "fecha_creacion", nullable = false, updatable = false)
    private LocalDateTime fechaCreacion;

    @PrePersist
    public void prePersist() {
        if (this.fechaCreacion == null) {
            this.fechaCreacion = LocalDateTime.now();
        }
    }
}
