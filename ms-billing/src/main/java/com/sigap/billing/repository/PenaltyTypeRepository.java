package com.sigap.billing.repository;

import com.sigap.billing.entity.PenaltyTypeEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PenaltyTypeRepository extends JpaRepository<PenaltyTypeEntity, Long> {

    boolean existsByCodeIgnoreCase(String code);

    Optional<PenaltyTypeEntity> findByPenaltyTypeIdAndActiveTrue(Long penaltyTypeId);

    List<PenaltyTypeEntity> findByActiveTrueOrderByNameAsc();
}
