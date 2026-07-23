package com.sigap.billing.service;

import com.sigap.billing.dto.penalty.ApplyWaterBillPenaltyRequest;
import com.sigap.billing.dto.penalty.WaterBillPenaltyResponse;

import java.util.List;

public interface WaterBillPenaltyService {

    WaterBillPenaltyResponse apply(Long billId, ApplyWaterBillPenaltyRequest request);

    List<WaterBillPenaltyResponse> findByBillId(Long billId);

}
