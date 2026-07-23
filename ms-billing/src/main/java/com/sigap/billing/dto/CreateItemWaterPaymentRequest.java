package com.sigap.billing.dto;

import com.sigap.billing.enums.PaymentMethod;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import java.util.List;

public record CreateItemWaterPaymentRequest(

        @NotNull(message = "El metodo de pago es obligatorio")
        PaymentMethod paymentMethod,

        LocalDate paymentDate,

        @Size(max = 500, message = "La observación no puede superar 500 caracteres")
        String observation,

        @Valid
        @NotEmpty(message = "Debe enviar al menos un item para cobrar")
        List<CreateItemWaterPaymentDetailRequest> items

) {
}
