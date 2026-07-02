package com.sigap.billing.dto;

import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDate;

public record UpdateWaterBillRequest(
        @PositiveOrZero(message = "La multa no puede ser negativa")
        BigDecimal penaltyAmount,

        @PositiveOrZero(message = "El descuento no puede ser negativo")
        BigDecimal discountAmount,

        LocalDate dueDate,

        @Size(max = 500, message = "La observación no puede superar 500 caracteres")
        String observation
) {
}