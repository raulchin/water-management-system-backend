package com.sigap.meters.client;

import com.sigap.meters.dto.PartnerApiResponse;
import com.sigap.meters.dto.PartnerResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "ms-partner", path = "/ms-partner")
public interface PartnerClient {

    @GetMapping("/api/v1/partners/{partnerId}")
    PartnerApiResponse<PartnerResponse> findById(@PathVariable Long partnerId);

    /**
     * Consulta el socio en ms-partner usando la cedula/RUC.
     * @param taxIdentification
     * @return
     */
    @GetMapping("/api/v1/partners/tax-identification/{taxIdentification}")
    PartnerApiResponse<PartnerResponse> findByTaxIdentification(
            @PathVariable String taxIdentification
    );


}
