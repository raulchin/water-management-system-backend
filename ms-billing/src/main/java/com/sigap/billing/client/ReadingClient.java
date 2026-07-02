package com.sigap.billing.client;

import com.sigap.billing.dto.ApiResponse;
import com.sigap.billing.dto.MeterReadingResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "ms-readings", url = "${clients.ms-readings.url}")
public interface ReadingClient {

    @GetMapping("/ms-readings/api/v1/lecturas-medidor/{readingId}")
    ApiResponse<MeterReadingResponse> findById(@PathVariable Long readingId);

}
