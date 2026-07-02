package com.sigap.billing.entity;

import com.sigap.billing.enums.WaterBillStatus;
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
@Table(
        name = "facturas_agua",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_factura_lectura", columnNames = "lectura_id"),
                @UniqueConstraint(name = "uk_factura_medidor_periodo", columnNames = {"medidor_id", "periodo"})
        },
        indexes = {
                @Index(name = "idx_facturas_identificacion_periodo", columnList = "identificacion_socio, periodo"),
                @Index(name = "idx_facturas_medidor_periodo", columnList = "numero_medidor, periodo"),
                @Index(name = "idx_facturas_estado", columnList = "estado")
        }
)
public class WaterBillEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "factura_id")
    private Long billId;

    @Column(name = "lectura_id", nullable = false)
    private Long readingId;

    @Column(name = "medidor_id", nullable = false)
    private Long meterId;

    @Column(name = "asignacion_id")
    private Long assignmentId;

    @Column(name = "socio_id", nullable = false)
    private Long partnerId;

    @Column(name = "periodo", nullable = false, length = 7)
    private String period;

    @Column(name = "identificacion_socio", length = 20)
    private String partnerIdentification;

    @Column(name = "nombre_socio", length = 200)
    private String partnerName;

    @Column(name = "numero_medidor", length = 50)
    private String meterNumber;

    @Column(name = "consumo_calculado", nullable = false, precision = 12, scale = 2)
    private BigDecimal calculatedConsumption;

    @Column(name = "tarifa_base", nullable = false, precision = 12, scale = 2)
    private BigDecimal baseFee;

    @Column(name = "valor_consumo", nullable = false, precision = 12, scale = 2)
    private BigDecimal consumptionAmount;

    @Column(name = "valor_multa", nullable = false, precision = 12, scale = 2)
    private BigDecimal penaltyAmount;

    @Column(name = "valor_descuento", nullable = false, precision = 12, scale = 2)
    private BigDecimal discountAmount;

    @Column(name = "valor_total", nullable = false, precision = 12, scale = 2)
    private BigDecimal totalAmount;

    @Column(name = "valor_pagado", nullable = false, precision = 12, scale = 2)
    private BigDecimal paidAmount;

    @Column(name = "saldo_pendiente", nullable = false, precision = 12, scale = 2)
    private BigDecimal pendingBalance;

    @Column(name = "fecha_emision", nullable = false)
    private LocalDate issueDate;

    @Column(name = "fecha_vencimiento")
    private LocalDate dueDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado", nullable = false, length = 20)
    private WaterBillStatus status;

    @Column(name = "observacion", length = 500)
    private String observation;

    @Column(name = "fecha_creacion", nullable = false, updatable = false)
    private LocalDateTime creationDate;

    @Column(name = "fecha_actualizacion")
    private LocalDateTime updateDate;

    @PrePersist
    public void prePersist() {
        if (issueDate == null) {
            issueDate = LocalDate.now();
        }

        if (status == null) {
            status = WaterBillStatus.PENDIENTE;
        }

        if (paidAmount == null) {
            paidAmount = BigDecimal.ZERO;
        }

        normalizeAmounts();
        calculateTotals();

        if (creationDate == null) {
            creationDate = LocalDateTime.now();
        }
    }

    @PreUpdate
    public void preUpdate() {
        normalizeAmounts();
        calculateTotals();
        updateDate = LocalDateTime.now();
    }

    private void normalizeAmounts() {
        if (calculatedConsumption == null) calculatedConsumption = BigDecimal.ZERO;
        if (baseFee == null) baseFee = BigDecimal.ZERO;
        if (consumptionAmount == null) consumptionAmount = BigDecimal.ZERO;
        if (penaltyAmount == null) penaltyAmount = BigDecimal.ZERO;
        if (discountAmount == null) discountAmount = BigDecimal.ZERO;
        if (paidAmount == null) paidAmount = BigDecimal.ZERO;
    }

    private void calculateTotals() {
        totalAmount = baseFee
                .add(consumptionAmount)
                .add(penaltyAmount)
                .subtract(discountAmount);

        pendingBalance = totalAmount.subtract(paidAmount);
    }
}