package com.sigap.meters.dto;

public record MeterAssignmentSummaryResponse(
        Long asignacionId,
        Long medidorId,
        String numeroMedidor,
        String marcaMedidor,
        String modeloMedidor,
        String estadoAsignacion
) {
}