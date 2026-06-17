package com.sigap.authserver.dto.menu;

import jakarta.validation.constraints.NotEmpty;

import java.util.List;

public record AssignMenusToRoleRequest(

        @NotEmpty(message = "Debe seleccionar al menos un menú")
        List<Long> menuIds

) {
}
