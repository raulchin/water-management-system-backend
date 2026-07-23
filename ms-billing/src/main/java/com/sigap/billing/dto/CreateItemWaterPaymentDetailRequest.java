package com.sigap.billing.dto;

import com.sigap.billing.enums.PaymentItemType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record CreateItemWaterPaymentDetailRequest(

        @NotNull(message = "El id de la factura es obligatorio")
        Long billId,

        Long billPenaltyId,

        @NotNull(message = "El tipo de item es obligatorio")
        PaymentItemType itemType,

        @NotNull(message = "El monto pagado es obligatorio")
        @Positive(message = "El monto pagado debe ser mayor a cero")
        BigDecimal paymentAmount

) {
}
