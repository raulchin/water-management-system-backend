package com.sigap.billing.repository;

import com.sigap.billing.entity.WaterPaymentEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface WaterPaymentRepository extends JpaRepository<WaterPaymentEntity, Long> {

    List<WaterPaymentEntity> findByBillIdOrderByPaymentDateDesc(Long billId);

    List<WaterPaymentEntity> findTop10ByOrderByCreationDateDesc();

    List<WaterPaymentEntity> findAllByOrderByCreationDateDesc();
}
