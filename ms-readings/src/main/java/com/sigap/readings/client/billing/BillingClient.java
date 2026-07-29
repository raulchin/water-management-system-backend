package com.sigap.readings.client.billing;

import com.sigap.readings.dto.ApiResponse;
import com.sigap.readings.dto.CreateWaterBillFromReadingRequest;
import com.sigap.readings.dto.RecalculateWaterBillFromReadingRequest;
import com.sigap.readings.dto.WaterBillResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.PostMapping;

@FeignClient(
        name = "ms-billing",
        url = "${clients.ms-billing.url}"
)
public interface BillingClient {

    @PostMapping("/ms-billing/api/v1/facturas/from-reading")
    ApiResponse<WaterBillResponse> createFromReading(
            @RequestBody CreateWaterBillFromReadingRequest request
    );

    @GetMapping("/ms-billing/api/v1/facturas/from-reading/{readingId}/can-recalculate")
    ApiResponse<Void> validateCanRecalculateFromReading(@PathVariable Long readingId);

    @PostMapping("/ms-billing/api/v1/facturas/from-reading/{readingId}/recalculate")
    ApiResponse<WaterBillResponse> recalculateFromReading(
            @PathVariable Long readingId,
            @RequestBody RecalculateWaterBillFromReadingRequest request
    );
}
