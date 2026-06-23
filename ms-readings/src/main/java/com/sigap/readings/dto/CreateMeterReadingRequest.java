package com.sigap.readings.dto;

import com.sigap.readings.enums.MeterReadingStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDate;

public record CreateMeterReadingRequest(
        @NotNull(message = "El id del medidor es obligatorio")
        Long meterId,

        Long assignmentId,

        Long partnerId,

        @NotBlank(message = "El periodo es obligatorio")
        @Size(min = 7, max = 7, message = "El periodo debe tener formato yyyy-MM")
        String period,

        LocalDate readingDate,

        @PositiveOrZero(message = "La lectura anterior no puede ser negativa")
        BigDecimal previousReading,

        @NotNull(message = "La lectura actual es obligatoria")
        @PositiveOrZero(message = "La lectura actual no puede ser negativa")
        BigDecimal currentReading,

        MeterReadingStatus status,

        @Size(max = 500, message = "La observación no puede superar 500 caracteres")
        String observation
) {
}
