package com.sigap.billing.service;

import com.sigap.billing.dto.CreateWaterBillRequest;
import com.sigap.billing.dto.UpdateWaterBillRequest;
import com.sigap.billing.dto.WaterBillResponse;

import java.util.List;

public interface WaterBillService {

    WaterBillResponse create(CreateWaterBillRequest request);

    WaterBillResponse findById(Long billId);

    List<WaterBillResponse> findByPartnerAndPeriod(String identification, String period);

    List<WaterBillResponse> findByMeterAndPeriod(String meterNumber, String period);

    List<WaterBillResponse> findPendingByPartner(String identification);

    WaterBillResponse update(Long billId, UpdateWaterBillRequest request);

    void cancel(Long billId);
}