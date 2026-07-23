package com.sigap.billing.dto;

import java.math.BigDecimal;

public record PendingPaymentItemResponse(

        String itemType,
        Long itemId,
        String description,
        BigDecimal amount,
        BigDecimal paidAmount,
        BigDecimal pendingAmount
) {
}
