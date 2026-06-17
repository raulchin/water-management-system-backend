package com.sigap.authserver.dto.menu;

import java.util.List;

public record MenuResponse(
        Long menuId,
        String name,
        String description,
        String path,
        String icon,
        String code,
        Long parentId,
        Integer displayOrder,
        Boolean active,
        List<MenuResponse> children
) {
}
