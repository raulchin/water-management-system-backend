package com.sigap.billing.repository;

import com.sigap.billing.entity.WaterBillPenaltyEntity;
import com.sigap.billing.enums.PenaltyStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface WaterBillPenaltyRepository extends JpaRepository<WaterBillPenaltyEntity, Long> {

    List<WaterBillPenaltyEntity> findByBillIdOrderByCreationDateDesc(Long billId);

    List<WaterBillPenaltyEntity> findByBillIdAndStatus(Long billId, PenaltyStatus status);

}
