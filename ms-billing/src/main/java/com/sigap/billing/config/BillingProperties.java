package com.sigap.billing.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
@ConfigurationProperties(prefix = "billing")
@Setter
@Getter
public class BillingProperties {

    private BigDecimal baseFee = BigDecimal.ZERO;
    private BigDecimal consumptionUnitPrice = BigDecimal.ZERO;
    private Integer dueDays = 15;

    private BigDecimal includedConsumption = BigDecimal.TEN;
    private BigDecimal minimumConsumptionAmount = BigDecimal.TEN;
    private BigDecimal excessUnitPrice = BigDecimal.ONE;
}
