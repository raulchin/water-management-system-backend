package com.sigap.partner.dto.partner;

import com.sigap.partner.dto.account.ContractAccountResponse;

import java.time.LocalDateTime;
import java.util.List;

public record PartnerResponse(
        Long idPartner,
        String taxIdentification,
        String names,
        String lastName,
        String address,
        String phone,
        String email,
        Boolean status,
        LocalDateTime registrationDate,
        LocalDateTime updateDate,
        List<ContractAccountResponse> contractAccounts
) {
}
