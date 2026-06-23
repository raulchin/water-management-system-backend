package com.sigap.readings.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record PartnerMeterResponse(
        Long asignacionId,
        Long medidorId,
        String numeroMedidor,
        Long socioId,
        LocalDate fechaAsignacion,
        LocalDate fechaRetiro,
        String estado,
        String observacion,
        LocalDateTime fechaActualizacion,
        String marcaMedidor,
        String modeloMedidor,
        String identificacionSocio
) {
}
