package com.sigap.partner.dto.account;

import java.time.LocalDateTime;

public record ContractAccountResponse(
        Long id,
        Long partnerId,
        String contractNumber,
        String address,
        String status,
        LocalDateTime creationDate,
        LocalDateTime updateDate
) {
}
