package com.sigap.billing.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public record WaterBillResponse(
        Long billId,
        Long readingId,
        Long meterId,
        Long assignmentId,
        Long partnerId,
        String period,
        String partnerIdentification,
        String partnerName,
        String meterNumber,
        BigDecimal calculatedConsumption,
        BigDecimal baseFee,
        BigDecimal consumptionAmount,
        BigDecimal penaltyAmount,
        BigDecimal discountAmount,
        BigDecimal totalAmount,
        BigDecimal paidAmount,
        BigDecimal pendingBalance,
        LocalDate issueDate,
        LocalDate dueDate,
        String status,
        String observation,
        LocalDateTime creationDate,
        LocalDateTime updateDate
) {
}