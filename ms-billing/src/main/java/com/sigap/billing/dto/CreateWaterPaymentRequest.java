package com.sigap.billing.dto;

import com.sigap.billing.enums.PaymentMethod;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDate;

public record CreateWaterPaymentRequest(

        @NotNull(message = "El id de la factura es obligatorio")
        Long billId,

        @NotNull(message = "El monto pagado es obligatorio")
        @Positive(message = "El monto pagado debe ser mayor a cero")
        BigDecimal paymentAmount,

        @NotNull(message = "El metodo de pago es obligatorio")
        PaymentMethod paymentMethod,

        LocalDate paymentDate,

        @Size(max = 500, message = "La observación no puede superar 500 caracteres")
        String observation

) {
}
