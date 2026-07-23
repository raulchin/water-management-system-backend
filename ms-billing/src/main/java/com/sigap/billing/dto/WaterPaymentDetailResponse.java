package com.sigap.billing.dto;

import java.math.BigDecimal;

public record WaterPaymentDetailResponse(
        Long paymentDetailId,
        Long billId,
        Long billPenaltyId,
        String itemType,
        String description,
        BigDecimal paymentAmount
) {
}
