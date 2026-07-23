package com.sigap.billing.dto.penalty;

import java.math.BigDecimal;

public record PenaltyTypeResponse(
        Long penaltyTypeId,
        String code,
        String name,
        String description,
        BigDecimal baseAmount,
        Boolean active
) {
}
