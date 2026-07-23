package com.sigap.billing.entity;

import com.sigap.billing.enums.PaymentItemType;
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
@Table(name = "pago_detalles")
public class WaterPaymentDetailEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "pago_detalle_id")
    private Long paymentDetailId;

    @Column(name = "pago_id", nullable = false)
    private Long paymentId;

    @Column(name = "factura_id", nullable = false)
    private Long billId;

    @Column(name = "factura_multa_id")
    private Long billPenaltyId;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_item", nullable = false, length = 30)
    private PaymentItemType itemType;

    @Column(name = "descripcion", nullable = false, length = 200)
    private String description;

    @Column(name = "monto_pagado", nullable = false, precision = 12, scale = 2)
    private BigDecimal paymentAmount;

    @Column(name = "fecha_creacion", nullable = false, updatable = false)
    private LocalDateTime creationDate;

    @PrePersist
    public void prePersist() {
        if (creationDate == null) {
            creationDate = LocalDateTime.now();
        }
    }
}
