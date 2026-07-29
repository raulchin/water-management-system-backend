package com.sigap.billing.service;

import com.sigap.billing.dto.*;

import java.util.List;

public interface WaterBillService {

    WaterBillResponse create(CreateWaterBillRequest request);

    WaterBillResponse createFromReading(CreateWaterBillFromReadingRequest request);

    WaterBillResponse findById(Long billId);

    List<WaterBillResponse> findByPartnerAndPeriod(String identification, String period);

    List<WaterBillResponse> findByMeterAndPeriod(String meterNumber, String period);

    List<WaterBillResponse> findPendingByPartner(String identification);

    WaterBillResponse update(Long billId, UpdateWaterBillRequest request);

    void cancel(Long billId);

    List<WaterBillResponse> findLast10();

    void validateCanRecalculateFromReading(Long readingId);

    WaterBillResponse recalculateFromReading(RecalculateWaterBillFromReadingRequest request);
}