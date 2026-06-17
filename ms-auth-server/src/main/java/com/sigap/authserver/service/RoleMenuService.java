package com.sigap.authserver.service;

import com.sigap.authserver.dto.menu.AssignMenusToRoleRequest;
import com.sigap.authserver.dto.menu.MenuResponse;

import java.util.List;

public interface RoleMenuService {

    List<MenuResponse> findMenusByRole(Long roleId);

    List<Long> findMenuIdsByRole(Long roleId);

    void assignMenusToRole(Long roleId, AssignMenusToRoleRequest request);
}
