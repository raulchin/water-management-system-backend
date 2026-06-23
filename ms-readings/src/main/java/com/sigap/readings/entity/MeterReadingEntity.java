package com.sigap.readings.entity;

import com.sigap.readings.enums.MeterReadingStatus;
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
        name = "lecturas_medidor",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_lectura_medidor_periodo",
                        columnNames = {"medidor_id", "periodo"}
                )
        }
)
public class MeterReadingEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "lectura_id")
    private Long readingId;

    @Column(name = "medidor_id", nullable = false)
    private Long meterId;

    @Column(name = "asignacion_id")
    private Long assignmentId;

    @Column(name = "socio_id")
    private Long partnerId;

    @Column(name = "periodo", nullable = false, length = 7)
    private String period;

    @Column(name = "fecha_lectura", nullable = false)
    private LocalDate readingDate;

    @Column(name = "lectura_anterior", nullable = false, precision = 12, scale = 2)
    private BigDecimal previousReading;

    @Column(name = "lectura_actual", nullable = false, precision = 12, scale = 2)
    private BigDecimal currentReading;

    @Column(name = "consumo_calculado", nullable = false, precision = 12, scale = 2)
    private BigDecimal calculatedConsumption;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado", nullable = false, length = 20)
    private MeterReadingStatus status;

    @Column(name = "observacion", length = 500)
    private String observation;

    @Column(name = "fecha_creacion", nullable = false, updatable = false)
    private LocalDateTime creationDate;

    @Column(name = "fecha_actualizacion")
    private LocalDateTime updateDate;

    @PrePersist
    public void prePersist() {
        if (readingDate == null) {
            readingDate = LocalDate.now();
        }

        if (previousReading == null) {
            previousReading = BigDecimal.ZERO;
        }

        if (status == null) {
            status = MeterReadingStatus.REGISTRADA;
        }

        calculateConsumption();

        if (creationDate == null) {
            creationDate = LocalDateTime.now();
        }
    }

    @PreUpdate
    public void preUpdate() {
        calculateConsumption();
        updateDate = LocalDateTime.now();
    }

    private void calculateConsumption() {
        if (currentReading == null) {
            return;
        }

        if (previousReading == null) {
            previousReading = BigDecimal.ZERO;
        }

        calculatedConsumption = currentReading.subtract(previousReading);
    }
}
