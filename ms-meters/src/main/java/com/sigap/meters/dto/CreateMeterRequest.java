package com.sigap.meters.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record CreateMeterRequest(

        @NotBlank(message = "El número de medidor es obligatorio")
        @Size(max = 50, message = "El número de medidor no debe superar los 50 caracteres")
        String numeroMedidor,

        @Size(max = 100, message = "La marca no debe superar los 100 caracteres")
        String marca,

        @Size(max = 100, message = "El modelo no debe superar los 100 caracteres")
        String modelo,

        @Size(max = 255, message = "La ubicación no debe superar los 255 caracteres")
        String ubicacion,

        @Size(max = 255, message = "La dirección de referencia no debe superar los 255 caracteres")
        String direccionReferencia,

        @PastOrPresent(message = "La fecha de instalación no puede ser futura")
        LocalDate fechaInstalacion,

        @Size(max = 20, message = "El estado no debe superar los 20 caracteres")
        String estado,

        @Size(max = 500, message = "La observación no debe superar los 500 caracteres")
        String observacion
) {
}
