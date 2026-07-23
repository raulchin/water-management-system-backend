package com.sigap.billing.dto.penalty;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record ApplyWaterBillPenaltyRequest(
        @NotNull Long penaltyTypeId,
        @Positive BigDecimal amount,
        @Size(max = 500) String observation
) {
}
