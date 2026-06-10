package com.sigap.partner.repository;

import com.sigap.partner.entity.PartnerEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PartnerRepository extends JpaRepository<PartnerEntity, Long> {

    boolean existsByTaxIdentification(String taxIdentification);

    boolean existsByTaxIdentificationAndIdPartnerNot(String taxIdentification, Long idPartner);

    Optional<PartnerEntity> findByTaxIdentification(String taxIdentification);

}
