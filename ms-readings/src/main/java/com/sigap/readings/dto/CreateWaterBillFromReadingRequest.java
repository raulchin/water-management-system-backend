package com.sigap.readings.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public record CreateWaterBillFromReadingRequest(

        Long readingId,
        Long meterId,
        Long assignmentId,
        Long partnerId,
        String period,
        String partnerIdentification,
        String partnerName,
        String meterNumber,
        BigDecimal calculatedConsumption,
        LocalDate readingDate,
        String observation

) {
}
