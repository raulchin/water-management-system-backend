package com.sigap.readings.client;

import com.sigap.readings.dto.ApiResponse;
import com.sigap.readings.dto.PartnerMeterResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(
        name = "ms-meters",
        url = "${clients.ms-meters.url}"
)
public interface MeterClient {

    @GetMapping("/ms-meters/api/v1/medidor-socios/{assignmentId}")
    ApiResponse<PartnerMeterResponse> findAssignmentById(@PathVariable Long assignmentId);

}
