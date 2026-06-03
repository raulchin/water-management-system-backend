package com.sigap.partner.mapper;

import com.sigap.partner.dto.partner.PartnerResponse;
import com.sigap.partner.entity.PartnerEntity;

import java.util.List;

public final class PartnerMapper {

    private PartnerMapper() {
    }

    public static PartnerResponse toResponse(PartnerEntity entity) {

        var accounts = entity.getContractAccounts() == null
                ? List.<com.sigap.partner.dto.account.ContractAccountResponse>of()
                : entity.getContractAccounts()
                .stream()
                .map(ContractAccountMapper::toResponse)
                .toList();

        return new PartnerResponse(
                entity.getIdPartner(),
                entity.getTaxIdentification(),
                entity.getNames(),
                entity.getLastName(),
                entity.getAddress(),
                entity.getPhone(),
                entity.getEmail(),
                entity.getStatus(),
                entity.getRegistrationDate(),
                entity.getUpdateDate(),
                accounts
        );
    }
}
