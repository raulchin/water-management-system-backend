
package com.sigap.meters.dto;

public record PartnerSummaryResponse(
        Long socioId,
        String identificacionSocio,
        String nombreSocio,
        String email
) {
}