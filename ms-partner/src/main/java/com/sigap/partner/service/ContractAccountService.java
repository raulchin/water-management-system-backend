package com.sigap.partner.service;

import com.sigap.partner.dto.account.ContractAccountCreateRequest;
import com.sigap.partner.dto.account.ContractAccountResponse;
import com.sigap.partner.dto.account.ContractAccountUpdateRequest;

import java.util.List;

public interface ContractAccountService {

    ContractAccountResponse create(ContractAccountCreateRequest request);

    List<ContractAccountResponse> findByPartnerId(Long partnerId);

    ContractAccountResponse findById(Long contractAccountId);

    ContractAccountResponse update(Long contractAccountId, ContractAccountUpdateRequest request);

    void delete(Long contractAccountId);
}
