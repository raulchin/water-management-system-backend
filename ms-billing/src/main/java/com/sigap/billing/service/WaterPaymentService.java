package com.sigap.billing.service;

import com.sigap.billing.dto.*;

import java.util.List;

public interface WaterPaymentService {

    WaterPaymentResponse create(CreateWaterPaymentRequest request);

    List<WaterPaymentResponse> findByBillId(Long billId);

    List<WaterPaymentResponse> findLast10();

    BatchWaterPaymentResponse createBatch(CreateBatchWaterPaymentRequest request);

    ItemWaterPaymentResponse createByItems(CreateItemWaterPaymentRequest request);

    List<PendingPaymentBillResponse> findPendingItemsByPartner(String identification);

    List<WaterPaymentResponse> findAllPaymentItems();

}
