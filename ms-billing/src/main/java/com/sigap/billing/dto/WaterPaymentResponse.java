package com.sigap.billing.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public record WaterPaymentResponse(

        Long paymentId,
        Long billId,
        Long partnerId,
        Long meterId,
        String partnerIdentification,
        String meterNumber,
        String period,
        BigDecimal paymentAmount,
        String paymentMethod,
        String reference,
        String paymentStatus,
        LocalDate paymentDate,
        String observation,
        BigDecimal billTotalAmount,
        BigDecimal billPaidAmount,
        BigDecimal billPendingBalance,
        String billStatus,
        LocalDateTime creationDate
) {
}
