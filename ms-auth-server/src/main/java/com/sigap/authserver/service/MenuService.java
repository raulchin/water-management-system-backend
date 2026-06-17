package com.sigap.authserver.service;

import com.sigap.authserver.dto.menu.CreateMenuRequest;
import com.sigap.authserver.dto.menu.MenuResponse;
import com.sigap.authserver.dto.menu.UpdateMenuRequest;

import java.util.List;

public interface MenuService {

    MenuResponse create(CreateMenuRequest request);

    MenuResponse findById(Long menuId);

    List<MenuResponse> findAll();

    List<MenuResponse> findActiveTree();

    MenuResponse update(Long menuId, UpdateMenuRequest request);

    void delete(Long menuId);
}
