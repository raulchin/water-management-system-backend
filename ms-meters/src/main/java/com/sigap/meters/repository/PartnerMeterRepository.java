package com.sigap.meters.repository;

import com.sigap.meters.entity.PartnerMeterEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PartnerMeterRepository extends JpaRepository<PartnerMeterEntity, Long> {

    List<PartnerMeterEntity> findBySocioId(Long socioId);

    List<PartnerMeterEntity> findByMedidor_MedidorId(Long medidorId);

    Optional<PartnerMeterEntity> findByMedidor_MedidorIdAndEstado(Long medidorId, String estado);

    boolean existsByMedidor_MedidorIdAndEstado(Long medidorId, String estado);
}
