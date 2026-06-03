package com.sigap.partner.mapper;

import com.sigap.partner.dto.account.ContractAccountResponse;
import com.sigap.partner.entity.ContractAccountEntity;

public final class ContractAccountMapper {

    private ContractAccountMapper() {
    }

    public static ContractAccountResponse toResponse(ContractAccountEntity entity) {
        Long partnerId = entity.getSocio() != null ? entity.getSocio().getIdPartner() : null;
        return new ContractAccountResponse(
                entity.getId(),
                partnerId,
                entity.getContractNumber(),
                entity.getAddress(),
                entity.getStatus(),
                entity.getCreationDate(),
                entity.getUpdateDate()
        );
    }
}
