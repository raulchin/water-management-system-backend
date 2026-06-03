package com.sigap.partner.service.impl;

import com.sigap.partner.dto.account.ContractAccountCreateRequest;
import com.sigap.partner.dto.account.ContractAccountResponse;
import com.sigap.partner.dto.account.ContractAccountUpdateRequest;
import com.sigap.partner.entity.ContractAccountEntity;
import com.sigap.partner.entity.PartnerEntity;
import com.sigap.partner.exception.BusinessException;
import com.sigap.partner.exception.ResourceNotFoundException;
import com.sigap.partner.mapper.ContractAccountMapper;
import com.sigap.partner.repository.ContractAccountRepository;
import com.sigap.partner.repository.PartnerRepository;
import com.sigap.partner.service.ContractAccountService;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ContractAccountServiceImpl implements ContractAccountService {

    private final ContractAccountRepository contractAccountRepository;

    private final PartnerRepository partnerRepository;

    @Override
    @Transactional
    public ContractAccountResponse create(ContractAccountCreateRequest request) {

        long partnerId = Long.parseLong(request.partnerId());
        validateContractNumberAvailability(request.contractNumber(), null);
        PartnerEntity partner = findPartner(partnerId);

        ContractAccountEntity entity = new ContractAccountEntity();
        entity.setSocio(partner);
        applyChanges(entity, request);

        return ContractAccountMapper.toResponse(contractAccountRepository.save(entity));
    }

    @Override
    public List<ContractAccountResponse> findByPartnerId(Long partnerId) {

        findPartner(partnerId);
        return contractAccountRepository.findBySocioIdPartner(partnerId)
                .stream()
                .map(ContractAccountMapper::toResponse)
                .toList();
    }

    @Override
    public ContractAccountResponse findById(Long contractAccountId) {

        return ContractAccountMapper.toResponse(findContractAccount(contractAccountId));

    }

    @Override
    @Transactional
    public ContractAccountResponse update(Long contractAccountId, ContractAccountUpdateRequest request) {
        ContractAccountEntity entity = findContractAccount(contractAccountId);
        applyChanges(entity, request);
        return ContractAccountMapper.toResponse(entity);
    }

    @Override
    @Transactional
    public void delete(Long contractAccountId) {

        contractAccountRepository.delete(findContractAccount(contractAccountId));

    }

    private PartnerEntity findPartner(Long partnerId) {
        return partnerRepository.findById(partnerId)
                .orElseThrow(() -> new ResourceNotFoundException("Socio no encontrado con id " + partnerId));
    }

    private void validateContractNumberAvailability(String contractNumber, Long accountId) {
        boolean exists = accountId == null
                ? contractAccountRepository.existsByContractNumber(contractNumber)
                : contractAccountRepository.existsByContractNumberAndIdNot(contractNumber, accountId);

        if (exists) {
            throw new BusinessException("Ya existe una cuenta contrato con el numero " + contractNumber);
        }
    }

    private ContractAccountEntity findContractAccount(Long contractAccountId) {
        return contractAccountRepository.findById(contractAccountId)
                .orElseThrow(() -> new ResourceNotFoundException("Cuenta contrato no encontrada con id " + contractAccountId));
    }


    private void applyChanges(ContractAccountEntity entity, ContractAccountCreateRequest request) {
        entity.setContractNumber(request.contractNumber());
        entity.setAddress(request.address());
        entity.setStatus(request.status());
    }

    private void applyChanges(ContractAccountEntity entity, ContractAccountUpdateRequest request) {
        entity.setAddress(request.address());
        entity.setStatus(request.status());
    }
}
