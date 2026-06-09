package com.sigap.meters.dto;

import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record UpdatePartnerMeterRequest(

        LocalDate fechaAsignacion,

        LocalDate fechaRetiro,

        String estado,

        @Size(max = 500, message = "La observación no debe superar los 500 caracteres")
        String observacion
) {
}
