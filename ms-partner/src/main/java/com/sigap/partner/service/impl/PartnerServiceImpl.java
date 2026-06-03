package com.sigap.partner.service.impl;

import com.sigap.partner.dto.partner.PartnerCreateRequest;
import com.sigap.partner.dto.partner.PartnerResponse;
import com.sigap.partner.dto.partner.PartnerUpdateRequest;
import com.sigap.partner.entity.PartnerEntity;
import com.sigap.partner.exception.BusinessException;
import com.sigap.partner.exception.ResourceNotFoundException;
import com.sigap.partner.mapper.PartnerMapper;
import com.sigap.partner.repository.ContractAccountRepository;
import com.sigap.partner.repository.PartnerRepository;
import com.sigap.partner.service.PartnerService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class PartnerServiceImpl implements PartnerService {

    private final PartnerRepository partnerRepository;

    private final ContractAccountRepository contractAccountRepository;

    @Override
    @Transactional
    public PartnerResponse create(PartnerCreateRequest request) {

        validateTaxIdentificationAvailability(request.taxIdentification(), null);
        PartnerEntity entity = new PartnerEntity();
        applyChanges(entity, request);
        return PartnerMapper.toResponse(partnerRepository.save(entity));
    }

    @Override
    public List<PartnerResponse> findAll() {
        return partnerRepository.findAll()
                .stream()
                .map(PartnerMapper::toResponse)
                .toList();
    }

    @Override
    public PartnerResponse findById(Long partnerId) {

        return PartnerMapper.toResponse(findPartnerEntity(partnerId));

    }

    @Override
    @Transactional
    public PartnerResponse update(Long partnerId, PartnerUpdateRequest request) {
        PartnerEntity entity = findPartnerEntity(partnerId);
        validateTaxIdentificationAvailability(request.taxIdentification(), partnerId);
        applyChanges(entity, request);
        return PartnerMapper.toResponse(entity);
    }

    @Override
    @Transactional
    public void delete(Long partnerId) {
        PartnerEntity entity = findPartnerEntity(partnerId);
        if (contractAccountRepository.existsBySocioIdPartner(partnerId)) {
            throw new BusinessException("No se puede eliminar el socio porque tiene cuentas contrato asociadas");
        }
        partnerRepository.delete(entity);
    }

    private void validateTaxIdentificationAvailability(String taxIdentification, Long partnerId) {
        boolean exists = partnerId == null
                ? partnerRepository.existsByTaxIdentification(taxIdentification)
                : partnerRepository.existsByTaxIdentificationAndIdPartnerNot(taxIdentification, partnerId);

        if (exists) {
            log.warn("Ya existe un socio con la cedula o RUC: {}", taxIdentification);
            throw new BusinessException("Ya existe un socio con la cedula o RUC " + taxIdentification);
        }
    }

    private void applyChanges(PartnerEntity entity, PartnerCreateRequest request) {
        entity.setTaxIdentification(request.taxIdentification());
        entity.setNames(request.names());
        entity.setLastName(request.lastName());
        entity.setAddress(request.address());
        entity.setPhone(request.phone());
        entity.setEmail(request.email());
        entity.setStatus(request.status() != null ? request.status() : Boolean.TRUE);
    }

    private void applyChanges(PartnerEntity entity, PartnerUpdateRequest request) {
        entity.setTaxIdentification(request.taxIdentification());
        entity.setNames(request.names());
        entity.setLastName(request.lastName());
        entity.setAddress(request.address());
        entity.setPhone(request.phone());
        entity.setEmail(request.email());
        entity.setStatus(request.status() != null ? request.status() : entity.getStatus());
    }

    private PartnerEntity findPartnerEntity(Long partnerId) {
    return partnerRepository.findById(partnerId)
            .orElseThrow(() -> new ResourceNotFoundException("Socio no encontrado con id " + partnerId));
    }
}
