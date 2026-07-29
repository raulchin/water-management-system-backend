package com.sigap.billing.repository;

import com.sigap.billing.entity.WaterBillEntity;
import com.sigap.billing.enums.WaterBillStatus;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface WaterBillRepository extends JpaRepository<WaterBillEntity, Long> {

    boolean existsByReadingId(Long readingId);

    boolean existsByMeterIdAndPeriod(Long meterId, String period);

    List<WaterBillEntity> findByPartnerIdentificationAndPeriod(String partnerIdentification, String period);

    List<WaterBillEntity> findByMeterNumberAndPeriod(String meterNumber, String period);

    List<WaterBillEntity> findByPartnerIdentificationAndStatusIn(
            String partnerIdentification,
            Collection<WaterBillStatus> statuses
    );

    List<WaterBillEntity> findTop10ByOrderByCreationDateDesc();

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select bill from WaterBillEntity bill where bill.billId = :billId")
    Optional<WaterBillEntity> findByIdForUpdate(@Param("billId") Long billId);

    Optional<WaterBillEntity> findByReadingId(Long readingId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select bill from WaterBillEntity bill where bill.readingId = :readingId")
    Optional<WaterBillEntity> findByReadingIdForUpdate(@Param("readingId") Long readingId);
}