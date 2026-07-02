package com.sigap.readings.dto;

public record PartnerSummaryResponse(
        Long socioId,
        String identificacionSocio,
        String nombreSocio,
        String email
) {
}