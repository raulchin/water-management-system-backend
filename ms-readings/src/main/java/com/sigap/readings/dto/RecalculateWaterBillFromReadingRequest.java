package com.sigap.readings.dto;

import java.math.BigDecimal;

public record RecalculateWaterBillFromReadingRequest(

        Long readingId,
        Long meterId,
        Long assignmentId,
        Long partnerId,
        String period,
        String partnerIdentification,
        String partnerName,
        String meterNumber,
        BigDecimal calculatedConsumption,
        String observation

) {
}
