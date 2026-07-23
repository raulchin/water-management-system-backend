package com.sigap.billing.service.impl;


import com.sigap.billing.dto.penalty.CreatePenaltyTypeRequest;
import com.sigap.billing.dto.penalty.PenaltyTypeResponse;
import com.sigap.billing.entity.PenaltyTypeEntity;
import com.sigap.billing.exception.DuplicateResourceException;
import com.sigap.billing.repository.PenaltyTypeRepository;
import com.sigap.billing.service.PenaltyTypeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PenaltyTypeServiceImpl implements PenaltyTypeService {

    private final PenaltyTypeRepository penaltyTypeRepository;

    @Override
    @Transactional
    public PenaltyTypeResponse create(CreatePenaltyTypeRequest request) {
        if (penaltyTypeRepository.existsByCodeIgnoreCase(request.code())) {
            throw new DuplicateResourceException("Ya existe un tipo de multa con codigo " + request.code());
        }

        PenaltyTypeEntity entity = PenaltyTypeEntity.builder()
                .code(normalize(request.code()).toUpperCase())
                .name(normalize(request.name()))
                .description(normalize(request.description()))
                .baseAmount(request.baseAmount())
                .active(true)
                .build();

        return toResponse(penaltyTypeRepository.save(entity));
    }

    @Override
    public List<PenaltyTypeResponse> findActive() {
        return penaltyTypeRepository.findByActiveTrueOrderByNameAsc()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    private PenaltyTypeResponse toResponse(PenaltyTypeEntity entity) {
        return new PenaltyTypeResponse(
                entity.getPenaltyTypeId(),
                entity.getCode(),
                entity.getName(),
                entity.getDescription(),
                entity.getBaseAmount(),
                entity.getActive()
        );
    }

    private String normalize(String value) {
        return value == null ? null : value.trim();
    }
}
