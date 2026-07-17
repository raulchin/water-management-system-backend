package com.sigap.billing.entity;


import com.sigap.billing.enums.PaymentMethod;
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
        name = "pagos_agua",
        indexes = {
                @Index(name = "idx_pagos_factura", columnList = "factura_id"),
                @Index(name = "idx_pagos_fecha", columnList = "fecha_pago")
        }
)
public class WaterPaymentEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "pago_id")
    private Long paymentId;

    @Column(name = "factura_id", nullable = false)
    private Long billId;

    @Column(name = "socio_id", nullable = false)
    private Long partnerId;

    @Column(name = "medidor_id", nullable = false)
    private Long meterId;

    @Column(name = "periodo", nullable = false, length = 7)
    private String period;

    @Column(name = "valor_pagado", nullable = false, precision = 12, scale = 2)
    private BigDecimal paidValue;

    @Enumerated(EnumType.STRING)
    @Column(name = "metodo_pago", nullable = false, length = 30)
    private PaymentMethod paymentMethod;

    @Column(name = "referencia_pago", length = 100)
    private String paymentReference;

    @Column(name = "estado", nullable = false, length = 20)
    private String status;

    @Column(name = "fecha_pago", nullable = false)
    private LocalDate paymentDate;

    @Column(name = "observacion", length = 500)
    private String observation;

    @Column(name = "fecha_creacion", nullable = false, updatable = false)
    private LocalDateTime creationDate;

    @Column(name = "identificacion_socio", length = 20)
    private String partnerIdentification;

    @Column(name = "numero_medidor", length = 50)
    private String meterNumber;

    @PrePersist
    public void prePersist() {
        if (paymentDate == null) {
            paymentDate = LocalDate.now();
        }

        if (status == null || status.isBlank()) {
            status = "REGISTRADO";
        }

        if (creationDate == null) {
            creationDate = LocalDateTime.now();
        }
    }
}
