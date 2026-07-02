package com.sigap.readings.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public record MeterReadingSearchResponse(

        PartnerInfo partner,
        MeterInfo meter,
        ReadingInfo reading
) {

    public record PartnerInfo(
            Long partnerId,
            String identification,
            String fullName,
            String email
    ) {
    }

    public record MeterInfo(
            Long meterId,
            Long assignmentId,
            String meterNumber,
            String brand,
            String model
    ) {
    }

    public record ReadingInfo(
            Long readingId,
            String period,
            LocalDate readingDate,
            BigDecimal previousReading,
            BigDecimal currentReading,
            BigDecimal calculatedConsumption,
            String status,
            String observation
    ) {
    }
}
