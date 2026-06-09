package com.sigap.meters.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record MeterResponse(

        Long medidorId,
        String numeroMedidor,
        String marca,
        String modelo,
        String ubicacion,
        String direccionReferencia,
        LocalDate fechaInstalacion,
        String estado,
        String observacion,
        LocalDateTime fechaCreacion
) {
}
