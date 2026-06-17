package com.sigap.authserver.service.impl;

import com.sigap.authserver.dto.menu.CreateMenuRequest;
import com.sigap.authserver.dto.menu.MenuResponse;
import com.sigap.authserver.dto.menu.UpdateMenuRequest;
import com.sigap.authserver.exception.BadRequestException;
import com.sigap.authserver.exception.ResourceNotFoundException;
import com.sigap.authserver.infrastructure.entity.MenuEntity;
import com.sigap.authserver.repository.MenuRepository;
import com.sigap.authserver.service.MenuService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MenuServiceImpl implements MenuService {

    private final MenuRepository menuRepository;

    @Override
    @Transactional
    public MenuResponse create(CreateMenuRequest request) {
        validateCodeDoesNotExist(request.code());

        MenuEntity menu = MenuEntity.builder()
                .name(request.name())
                .description(request.description())
                .path(request.path())
                .icon(request.icon())
                .code(request.code().toUpperCase())
                .parent(findParent(request.parentId()))
                .displayOrder(getDisplayOrder(request.displayOrder()))
                .active(getActive(request.active()))
                .build();

        return toResponse(menuRepository.save(menu));
    }

    @Override
    @Transactional(readOnly = true)
    public MenuResponse findById(Long menuId) {
        return toResponse(findMenuById(menuId));
    }

    @Override
    @Transactional(readOnly = true)
    public List<MenuResponse> findAll() {
        return menuRepository.findAll()
                .stream()
                .sorted(Comparator.comparing(MenuEntity::getDisplayOrder))
                .map(this::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<MenuResponse> findActiveTree() {
        List<MenuEntity> menus = menuRepository.findByActiveTrueOrderByDisplayOrderAsc();

        return menus.stream()
                .filter(menu -> menu.getParent() == null)
                .map(menu -> toTreeResponse(menu, menus))
                .toList();
    }

    @Override
    @Transactional
    public MenuResponse update(Long menuId, UpdateMenuRequest request) {
        MenuEntity menu = findMenuById(menuId);

        updateMenuData(menu, request);

        return toResponse(menuRepository.save(menu));
    }

    @Override
    @Transactional
    public void delete(Long menuId) {
        MenuEntity menu = findMenuById(menuId);
        menu.setActive(false);
        menuRepository.save(menu);
    }

    private void validateCodeDoesNotExist(String code) {
        if (menuRepository.existsByCodeIgnoreCase(code)) {
            throw new BadRequestException("Ya existe un menu con el codigo: " + code);
        }
    }

    private MenuEntity findMenuById(Long menuId) {
        return menuRepository.findById(menuId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "No existe el menu con id: " + menuId
                ));
    }

    private MenuEntity findParent(Long parentId) {
        if (parentId == null) {
            return null;
        }

        return findMenuById(parentId);
    }

    private Integer getDisplayOrder(Integer displayOrder) {
        return displayOrder == null ? 0 : displayOrder;
    }

    private Boolean getActive(Boolean active) {
        return active == null || active;
    }

    private void updateMenuData(MenuEntity menu, UpdateMenuRequest request) {
        if (request.name() != null) {
            menu.setName(request.name());
        }

        if (request.description() != null) {
            menu.setDescription(request.description());
        }

        if (request.path() != null) {
            menu.setPath(request.path());
        }

        if (request.icon() != null) {
            menu.setIcon(request.icon());
        }

        if (request.displayOrder() != null) {
            menu.setDisplayOrder(request.displayOrder());
        }

        if (request.active() != null) {
            menu.setActive(request.active());
        }

        menu.setParent(findParent(request.parentId()));
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

    private MenuResponse toTreeResponse(MenuEntity menu, List<MenuEntity> allMenus) {
        List<MenuResponse> children = allMenus.stream()
                .filter(child -> isChildOf(child, menu))
                .map(child -> toTreeResponse(child, allMenus))
                .toList();

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
                children
        );
    }

    private boolean isChildOf(MenuEntity child, MenuEntity parent) {
        return child.getParent() != null
                && child.getParent().getMenuId().equals(parent.getMenuId());
    }
}
