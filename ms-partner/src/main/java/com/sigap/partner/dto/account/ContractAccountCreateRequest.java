package com.sigap.partner.dto.account;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record ContractAccountCreateRequest(
        @NotBlank(message = "El numero de contrato es obligatorio")
        @Size(max = 30, message = "El numero de contrato no puede superar 30 caracteres")
        String contractNumber,

        @NotBlank(message = "La direccion es obligatoria")
        @Size(max = 250, message = "La direccion no puede superar 250 caracteres")
        String address,

        LocalDate activationDate,

        @Size(max = 20, message = "El estado no puede superar 20 caracteres")
        String status,

        @NotBlank(message = "Codigo del socio es requerido")
        String partnerId


) {
}
