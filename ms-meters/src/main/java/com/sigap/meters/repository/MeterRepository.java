package com.sigap.meters.repository;

import com.sigap.meters.entity.MeterEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MeterRepository extends JpaRepository<MeterEntity, Long> {

    Optional<MeterEntity> findByNumeroMedidor(String numeroMedidor);

    boolean existsByNumeroMedidor(String numeroMedidor);

    boolean existsByNumeroMedidorIgnoreCase(String numeroMedidor);
}
