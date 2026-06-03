package com.sigap.authserver.dto.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record RegisterRequest(

        @NotBlank(message = "El username es obligatorio.")
        @Size(max = 100, message = "El username no puede exceder 100 caracteres.")
        String username,

        @NotBlank(message = "La contraseña es obligatoria.")
        @Size(min = 8, max = 100, message = "La contraseña debe tener entre 8 y 100 caracteres.")
        @Pattern(
                regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&._#-])[A-Za-z\\d@$!%*?&._#-]{8,100}$",
                message = "La contraseña debe incluir mayusculas, minusculas, numeros y un caracter especial."
        )
        String password,

        @NotBlank(message = "El correo es obligatorio.")
        @Email(message = "El correo no tiene un formato valido.")
        String email,

        @NotBlank(message = "Los nombres son obligatorios.")
        @Size(max = 100, message = "Los nombres no pueden exceder 100 caracteres.")
        String nombres,

        @NotBlank(message = "El rol del usuario es obligatorio.")
        String rol

) {
}
