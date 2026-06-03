package com.sigap.msreport.repository;

import com.sigap.msreport.bean.LoadBalancerConfiguration;
import com.sigap.msreport.model.PartnerModel;
import org.springframework.cloud.loadbalancer.annotation.LoadBalancerClient;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.Optional;

@FeignClient( name = "ms-partner")
@LoadBalancerClient( name = "ms-partner", configuration = LoadBalancerConfiguration.class)
public interface PartnerRepository {

    @GetMapping("/{partnerId}")
    Optional<PartnerModel> findById( @PathVariable Long partnerId);
}
