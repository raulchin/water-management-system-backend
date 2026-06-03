package com.sigap.partner.repository;

import com.sigap.partner.entity.ContractAccountEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;


public interface ContractAccountRepository extends JpaRepository<ContractAccountEntity, Long> {

    boolean existsBySocioIdPartner(Long partnerId);

    boolean existsByContractNumber(String contractNumber);

    boolean existsByContractNumberAndIdNot(String contractNumber, Long id);

    List<ContractAccountEntity> findBySocioIdPartner(Long partnerId);
}
