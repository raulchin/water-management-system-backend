package com.sigap.meters.client;

import com.sigap.meters.dto.PartnerApiResponse;
import com.sigap.meters.dto.PartnerResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "ms-partner")
public interface PartnerClient {

    @GetMapping("/ms-partner/api/v1/partners/{partnerId}")
    PartnerApiResponse<PartnerResponse> findById(@PathVariable Long partnerId);

}
