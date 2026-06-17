package com.sigap.authserver.controller;

import com.sigap.authserver.dto.ApiResponse;
import com.sigap.authserver.dto.menu.CreateMenuRequest;
import com.sigap.authserver.dto.menu.MenuResponse;
import com.sigap.authserver.dto.menu.UpdateMenuRequest;
import com.sigap.authserver.service.MenuService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/menus")
@RequiredArgsConstructor
public class MenuController {

    private final MenuService menuService;

    @PostMapping
    public ApiResponse<MenuResponse> create(@Valid @RequestBody CreateMenuRequest request) {
        return ApiResponse.success(
                "Menu creado correctamente",
                menuService.create(request)
        );
    }

    @GetMapping("/{menuId}")
    public ApiResponse<MenuResponse> findById(@PathVariable Long menuId) {
        return ApiResponse.success(
                "Menu obtenido correctamente",
                menuService.findById(menuId)
        );
    }

    @GetMapping
    public ApiResponse<List<MenuResponse>> findAll() {
        return ApiResponse.success(
                "Menus obtenidos correctamente",
                menuService.findAll()
        );
    }

    @GetMapping("/tree")
    public ApiResponse<List<MenuResponse>> findActiveTree() {
        return ApiResponse.success(
                "Arbol de menus obtenido correctamente",
                menuService.findActiveTree()
        );
    }

    @PutMapping("/{menuId}")
    public ApiResponse<MenuResponse> update(
            @PathVariable Long menuId,
            @Valid @RequestBody UpdateMenuRequest request
    ) {
        return ApiResponse.success(
                "Menu actualizado correctamente",
                menuService.update(menuId, request)
        );
    }

    @DeleteMapping("/{menuId}")
    public ApiResponse<Void> delete(@PathVariable Long menuId) {
        menuService.delete(menuId);

        return ApiResponse.success(
                "Menu eliminado correctamente",
                null
        );
    }


}
