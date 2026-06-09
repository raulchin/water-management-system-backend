package com.sigap.meters.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record CreatePartnerMeterRequest(

        @NotNull(message = "El id del medidor es obligatorio")
        Long medidorId,

        @NotNull(message = "El id del socio es obligatorio")
        Long socioId,

        LocalDate fechaAsignacion,

        @Size(max = 500, message = "La observación no debe superar los 500 caracteres")
        String observacion
) {
}
