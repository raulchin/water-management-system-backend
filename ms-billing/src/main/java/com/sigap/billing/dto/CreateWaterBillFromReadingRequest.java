package com.sigap.billing.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDate;

public record CreateWaterBillFromReadingRequest(

        @NotNull(message = "El id de la lectura es obligatorio")
        Long readingId,

        @NotNull(message = "El id del medidor es obligatorio")
        Long meterId,

        Long assignmentId,

        @NotNull(message = "El id del socio es obligatorio")
        Long partnerId,

        @NotBlank(message = "El periodo es obligatorio")
        @Size(min = 7, max = 7, message = "El periodo debe tener formato yyyy-MM")
        String period,

        @NotBlank(message = "La identificacion del socio es obligatoria")
        @Size(max = 20, message = "La identificacion del socio no puede superar 20 caracteres")
        String partnerIdentification,

        @Size(max = 200, message = "El nombre del socio no puede superar 200 caracteres")
        String partnerName,

        @NotBlank(message = "El numero de medidor es obligatorio")
        @Size(max = 50, message = "El numero de medidor no puede superar 50 caracteres")
        String meterNumber,

        @NotNull(message = "El consumo calculado es obligatorio")
        @PositiveOrZero(message = "El consumo calculado no puede ser negativo")
        BigDecimal calculatedConsumption,

        LocalDate readingDate,

        @Size(max = 500, message = "La observación no puede superar 500 caracteres")
        String observation

) {
}
