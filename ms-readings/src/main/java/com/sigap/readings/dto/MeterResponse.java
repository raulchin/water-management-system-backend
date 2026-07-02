package com.sigap.readings.dto;

public record MeterResponse(
        Long medidorId,
        String numeroMedidor,
        String marca,
        String modelo,
        String ubicacion,
        String direccionReferencia,
        String fechaInstalacion,
        String estado,
        String observacion,
        String fechaCreacion
) {
}