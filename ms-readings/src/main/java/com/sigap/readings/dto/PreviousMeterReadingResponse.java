package com.sigap.readings.dto;

import java.math.BigDecimal;

public record PreviousMeterReadingResponse(

        Long meterId,
        String period,
        String previousPeriod,
        BigDecimal previousReading,
        Boolean hasPreviousReading

) {
}
