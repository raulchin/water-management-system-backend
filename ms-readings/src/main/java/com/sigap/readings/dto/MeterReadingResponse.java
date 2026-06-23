package com.sigap.readings.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public record MeterReadingResponse(
        Long readingId,
        Long meterId,
        Long assignmentId,
        Long partnerId,
        String period,
        LocalDate readingDate,
        BigDecimal previousReading,
        BigDecimal currentReading,
        BigDecimal calculatedConsumption,
        String status,
        String observation,
        LocalDateTime creationDate,
        LocalDateTime updateDate,
        String meterNumber,
        String partnerIdentification
) {
}
