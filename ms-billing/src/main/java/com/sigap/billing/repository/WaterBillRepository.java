package com.sigap.billing.repository;

import com.sigap.billing.entity.WaterBillEntity;
import com.sigap.billing.enums.WaterBillStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;

public interface WaterBillRepository extends JpaRepository<WaterBillEntity, Long> {

    boolean existsByReadingId(Long readingId);

    boolean existsByMeterIdAndPeriod(Long meterId, String period);

    List<WaterBillEntity> findByPartnerIdentificationAndPeriod(String partnerIdentification, String period);

    List<WaterBillEntity> findByMeterNumberAndPeriod(String meterNumber, String period);

    List<WaterBillEntity> findByPartnerIdentificationAndStatusIn(
            String partnerIdentification,
            Collection<WaterBillStatus> statuses
    );
}