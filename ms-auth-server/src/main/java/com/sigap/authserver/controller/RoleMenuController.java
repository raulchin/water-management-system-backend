package com.sigap.authserver.controller;

import com.sigap.authserver.dto.ApiResponse;
import com.sigap.authserver.dto.menu.AssignMenusToRoleRequest;
import com.sigap.authserver.dto.menu.MenuResponse;
import com.sigap.authserver.service.RoleMenuService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/v1/roles")
@RequiredArgsConstructor
public class RoleMenuController {

    private final RoleMenuService roleMenuService;

    @GetMapping("/{roleId}/menus")
    public ApiResponse<List<MenuResponse>> findMenusByRole(@PathVariable Long roleId) {
        log.info("Obtener el listado del menu para el rol: {}", roleId);
        return ApiResponse.success(
                "Menus asignados al rol obtenidos correctamente",
                roleMenuService.findMenusByRole(roleId)
        );
    }

    @GetMapping("/{roleId}/menus/ids")
    public ApiResponse<List<Long>> findMenuIdsByRole(@PathVariable Long roleId) {
        return ApiResponse.success(
                "Ids de menus asignados al rol obtenidos correctamente",
                roleMenuService.findMenuIdsByRole(roleId)
        );
    }

    @PutMapping("/{roleId}/menus")
    public ApiResponse<Void> assignMenusToRole(
            @PathVariable Long roleId,
            @Valid @RequestBody AssignMenusToRoleRequest request
    ) {
        roleMenuService.assignMenusToRole(roleId, request);

        return ApiResponse.success(
                "Menus asignados al rol correctamente",
                null
        );
    }
}
