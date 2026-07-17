package com.sigap.billing.service;

import com.sigap.billing.dto.BatchWaterPaymentResponse;
import com.sigap.billing.dto.CreateBatchWaterPaymentRequest;
import com.sigap.billing.dto.CreateWaterPaymentRequest;
import com.sigap.billing.dto.WaterPaymentResponse;

import java.util.List;

public interface WaterPaymentService {

    WaterPaymentResponse create(CreateWaterPaymentRequest request);

    List<WaterPaymentResponse> findByBillId(Long billId);

    List<WaterPaymentResponse> findLast10();

    BatchWaterPaymentResponse createBatch(CreateBatchWaterPaymentRequest request);

}
