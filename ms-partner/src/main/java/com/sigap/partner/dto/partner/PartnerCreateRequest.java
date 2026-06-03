package com.sigap.partner.dto.partner;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record PartnerCreateRequest(

        @NotBlank(message = "La cedula o RUC es obligatoria")
        @Size(max = 20, message = "La cedula o RUC no puede superar 20 caracteres")
        String taxIdentification,

        @NotBlank(message = "Los nombres son obligatorios")
        @Size(max = 100, message = "Los nombres no pueden superar 100 caracteres")
        String names,

        @NotBlank(message = "Los apellidos son obligatorios")
        @Size(max = 100, message = "Los apellidos no pueden superar 100 caracteres")
        String lastName,

        @Size(max = 500, message = "La direccion no puede superar 500 caracteres")
        String address,

        @NotBlank(message = "El telefono es obligatorio")
        @Size(max = 20, message = "El telefono no puede superar 20 caracteres")
        String phone,

        @Email(message = "El correo no tiene un formato valido")
        @Size(max = 100, message = "El correo no puede superar 100 caracteres")
        String email,

        Boolean status
) {
}
