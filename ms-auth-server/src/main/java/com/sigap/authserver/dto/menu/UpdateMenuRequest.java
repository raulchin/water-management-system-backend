package com.sigap.authserver.dto.menu;

import jakarta.validation.constraints.Size;

public record UpdateMenuRequest(
        @Size(max = 100, message = "El nombre no debe superar los 100 caracteres")
        String name,

        @Size(max = 200, message = "La descripción no debe superar los 200 caracteres")
        String description,

        @Size(max = 200, message = "La ruta no debe superar los 200 caracteres")
        String path,

        @Size(max = 100, message = "El icono no debe superar los 100 caracteres")
        String icon,

        Long parentId,

        Integer displayOrder,

        Boolean active

) {
}
