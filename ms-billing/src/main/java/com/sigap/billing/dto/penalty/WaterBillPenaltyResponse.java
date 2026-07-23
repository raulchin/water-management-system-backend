package com.sigap.billing.dto.penalty;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public record WaterBillPenaltyResponse(

        Long billPenaltyId,
        Long billId,
        Long penaltyTypeId,
        String penaltyCode,
        String penaltyName,
        BigDecimal amount,
        String status,
        String observation,
        LocalDate applicationDate,
        LocalDateTime creationDate

) {
}
