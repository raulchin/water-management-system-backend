package com.sigap.meters.repository;

import com.sigap.meters.entity.MeterAssignmentEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PartnerMeterRepository extends JpaRepository<MeterAssignmentEntity, Long> {

    List<MeterAssignmentEntity> findBySocioId(Long socioId);

    List<MeterAssignmentEntity> findByMedidor_MedidorId(Long medidorId);

    Optional<MeterAssignmentEntity> findByMedidor_MedidorIdAndEstado(Long medidorId, String estado);

    boolean existsByMedidor_MedidorIdAndEstado(Long medidorId, String estado);
}
