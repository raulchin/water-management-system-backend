package com.sigap.billing.dto.penalty;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record CreatePenaltyTypeRequest(

        @NotBlank String code,
        @NotBlank String name,
        @Size(max = 300) String description,
        @NotNull @PositiveOrZero BigDecimal baseAmount

) {
}
