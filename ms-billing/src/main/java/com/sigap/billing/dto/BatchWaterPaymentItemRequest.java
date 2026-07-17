package com.sigap.billing.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record BatchWaterPaymentItemRequest(

        @NotNull(message = "El id de la factura es obligatorio")
        Long billId,

        @NotNull(message = "El monto pagado es obligatorio")
        @Positive(message = "El monto pagado debe ser mayor a cero")
        BigDecimal paymentAmount

) {
}
