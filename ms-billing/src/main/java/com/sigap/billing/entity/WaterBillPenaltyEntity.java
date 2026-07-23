package com.sigap.billing.entity;

import com.sigap.billing.enums.PenaltyStatus;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "factura_multas")
public class WaterBillPenaltyEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "factura_multa_id")
    private Long billPenaltyId;

    @Column(name = "factura_id", nullable = false)
    private Long billId;

    @Column(name = "tipo_multa_id", nullable = false)
    private Long penaltyTypeId;

    @Column(name = "socio_id", nullable = false)
    private Long partnerId;

    @Column(name = "medidor_id", nullable = false)
    private Long meterId;

    @Column(name = "periodo", nullable = false, length = 7)
    private String period;

    @Column(name = "identificacion_socio", length = 20)
    private String partnerIdentification;

    @Column(name = "numero_medidor", length = 50)
    private String meterNumber;

    @Column(name = "codigo_multa", nullable = false, length = 50)
    private String penaltyCode;

    @Column(name = "nombre_multa", nullable = false, length = 100)
    private String penaltyName;

    @Column(name = "monto", nullable = false, precision = 12, scale = 2)
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado", nullable = false, length = 20)
    private PenaltyStatus status;

    @Column(name = "observacion", length = 500)
    private String observation;

    @Column(name = "fecha_aplicacion", nullable = false)
    private LocalDate applicationDate;

    @Column(name = "fecha_creacion", nullable = false, updatable = false)
    private LocalDateTime creationDate;

    @Column(name = "fecha_actualizacion")
    private LocalDateTime updateDate;

    @PrePersist
    public void prePersist() {
        if (status == null) status = PenaltyStatus.ACTIVA;
        if (applicationDate == null) applicationDate = LocalDate.now();
        if (creationDate == null) creationDate = LocalDateTime.now();
    }

    @PreUpdate
    public void preUpdate() {
        updateDate = LocalDateTime.now();
    }
}
