package com.sigap.billing.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDate;

public record CreateWaterBillRequest(
        @NotNull(message = "El id de la lectura es obligatorio")
        Long readingId,

        @Size(max = 20, message = "La identificacion del socio no puede superar 20 caracteres")
        String partnerIdentification,

        @Size(max = 200, message = "El nombre del socio no puede superar 200 caracteres")
        String partnerName,

        @NotNull(message = "La tarifa base es obligatoria")
        @PositiveOrZero(message = "La tarifa base no puede ser negativa")
        BigDecimal baseFee,

        @NotNull(message = "El valor de consumo es obligatorio")
        @PositiveOrZero(message = "El valor de consumo no puede ser negativo")
        BigDecimal consumptionAmount,

        @PositiveOrZero(message = "La multa no puede ser negativa")
        BigDecimal penaltyAmount,

        @PositiveOrZero(message = "El descuento no puede ser negativo")
        BigDecimal discountAmount,

        LocalDate dueDate,

        @Size(max = 500, message = "La observación no puede superar 500 caracteres")
        String observation
) {
}