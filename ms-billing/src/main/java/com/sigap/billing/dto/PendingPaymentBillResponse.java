package com.sigap.billing.dto;

import java.util.List;

public record PendingPaymentBillResponse(
        Long billId,
        String period,
        String partnerIdentification,
        String partnerName,
        String meterNumber,
        String billStatus,
        List<PendingPaymentItemResponse> items
) {
}
