package com.sigap.billing.repository;

import com.sigap.billing.entity.WaterPaymentDetailEntity;
import com.sigap.billing.enums.PaymentItemType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;

public interface WaterPaymentDetailRepository extends JpaRepository<WaterPaymentDetailEntity, Long> {

    List<WaterPaymentDetailEntity> findByPaymentIdOrderByCreationDateAsc(Long paymentId);

    @Query("""
            select coalesce(sum(detail.paymentAmount), 0)
            from WaterPaymentDetailEntity detail
            where detail.billId = :billId
              and detail.itemType = :itemType
              and (
                    (:billPenaltyId is null and detail.billPenaltyId is null)
                    or detail.billPenaltyId = :billPenaltyId
              )
            """)
    BigDecimal sumPaidByItem(
            @Param("billId") Long billId,
            @Param("itemType") PaymentItemType itemType,
            @Param("billPenaltyId") Long billPenaltyId
    );
}
