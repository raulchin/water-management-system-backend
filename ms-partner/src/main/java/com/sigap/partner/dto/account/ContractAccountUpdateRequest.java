package com.sigap.partner.dto.account;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record ContractAccountUpdateRequest(
        @NotBlank(message = "La direccion es obligatoria")
        @Size(max = 250, message = "La direccion no puede superar 250 caracteres")
        String address,

        LocalDate activationDate,

        @NotBlank(message = "El estado es obligatorio")
        @Size(max = 20, message = "El estado no puede superar 20 caracteres")
        String status
) {
}
