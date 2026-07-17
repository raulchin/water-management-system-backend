package com.sigap.billing.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public record BatchWaterPaymentResponse(

        String reference,
        String paymentMethod,
        LocalDate paymentDate,
        BigDecimal totalPaidAmount,
        List<WaterPaymentResponse> items

) {
}
