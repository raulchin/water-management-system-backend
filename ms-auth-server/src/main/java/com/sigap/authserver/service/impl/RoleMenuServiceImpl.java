package com.sigap.authserver.service.impl;

import com.sigap.authserver.dto.menu.AssignMenusToRoleRequest;
import com.sigap.authserver.dto.menu.MenuResponse;
import com.sigap.authserver.exception.ResourceNotFoundException;
import com.sigap.authserver.infrastructure.entity.MenuEntity;
import com.sigap.authserver.infrastructure.entity.RoleEntity;
import com.sigap.authserver.infrastructure.entity.RoleMenuEntity;
import com.sigap.authserver.infrastructure.entity.RoleMenuId;
import com.sigap.authserver.repository.MenuRepository;
import com.sigap.authserver.repository.RoleMenuRepository;
import com.sigap.authserver.repository.RoleRepository;
import com.sigap.authserver.service.RoleMenuService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class RoleMenuServiceImpl  implements RoleMenuService {

    private final RoleRepository roleRepository;
    private final MenuRepository menuRepository;
    private final RoleMenuRepository roleMenuRepository;

    @Override
    @Transactional(readOnly = true)
    public List<MenuResponse> findMenusByRole(Long roleId) {
        validateRoleExists(roleId);

        return roleMenuRepository.findById_RoleId(roleId)
                .stream()
                .map(RoleMenuEntity::getMenu)
                .filter(MenuEntity::getActive)
                .sorted(Comparator.comparing(MenuEntity::getDisplayOrder))
                .map(this::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Long> findMenuIdsByRole(Long roleId) {
        validateRoleExists(roleId);

        return roleMenuRepository.findById_RoleId(roleId)
                .stream()
                .map(roleMenu -> roleMenu.getMenu().getMenuId())
                .toList();
    }

    @Override
    @Transactional
    public void assignMenusToRole(Long roleId, AssignMenusToRoleRequest request) {
        RoleEntity role = findRoleById(roleId);
        List<MenuEntity> menus = findMenus(request.menuIds());

        roleMenuRepository.deleteById_RoleId(roleId);

        List<RoleMenuEntity> roleMenus = menus.stream()
                .map(menu -> buildRoleMenu(role, menu))
                .toList();

        roleMenuRepository.saveAll(roleMenus);
    }

    private void validateRoleExists(Long roleId) {

        log.info("Validar si el rol existe: {}", roleId);
        if (!roleRepository.existsById(roleId)) {
            throw new ResourceNotFoundException("No existe el rol con id: " + roleId);
        }
    }

    private RoleEntity findRoleById(Long roleId) {
        return roleRepository.findById(roleId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "No existe el rol con id: " + roleId
                ));
    }

    private List<MenuEntity> findMenus(List<Long> menuIds) {
        List<MenuEntity> menus = menuRepository.findAllById(menuIds);

        if (menus.size() != menuIds.size()) {
            throw new ResourceNotFoundException(
                    "Uno o mas menus seleccionados no existen"
            );
        }

        return menus;
    }

    private RoleMenuEntity buildRoleMenu(RoleEntity role, MenuEntity menu) {
        RoleMenuId id = new RoleMenuId(role.getIdRol(), menu.getMenuId());

        return RoleMenuEntity.builder()
                .id(id)
                .role(role)
                .menu(menu)
                .build();
    }

    private MenuResponse toResponse(MenuEntity menu) {
        Long parentId = menu.getParent() == null ? null : menu.getParent().getMenuId();

        return new MenuResponse(
                menu.getMenuId(),
                menu.getName(),
                menu.getDescription(),
                menu.getPath(),
                menu.getIcon(),
                menu.getCode(),
                parentId,
                menu.getDisplayOrder(),
                menu.getActive(),
                List.of()
        );
    }
}
