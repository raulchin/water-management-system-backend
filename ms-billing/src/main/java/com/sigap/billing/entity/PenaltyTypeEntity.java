package com.sigap.billing.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "tipos_multa")
public class PenaltyTypeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "tipo_multa_id")
    private Long penaltyTypeId;

    @Column(name = "codigo", nullable = false, unique = true, length = 50)
    private String code;

    @Column(name = "nombre", nullable = false, length = 100)
    private String name;

    @Column(name = "descripcion", length = 300)
    private String description;

    @Column(name = "monto_base", nullable = false, precision = 12, scale = 2)
    private BigDecimal baseAmount;

    @Column(name = "activo", nullable = false)
    private Boolean active;

    @Column(name = "fecha_creacion", nullable = false, updatable = false)
    private LocalDateTime creationDate;

    @Column(name = "fecha_actualizacion")
    private LocalDateTime updateDate;

    @PrePersist
    public void prePersist() {
        if (active == null) active = true;
        if (baseAmount == null) baseAmount = BigDecimal.ZERO;
        if (creationDate == null) creationDate = LocalDateTime.now();
    }

    @PreUpdate
    public void preUpdate() {
        updateDate = LocalDateTime.now();
    }
}
