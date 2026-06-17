package com.sigap.authserver.repository;

import com.sigap.authserver.infrastructure.entity.MenuEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface MenuRepository extends JpaRepository<MenuEntity, Long> {

    boolean existsByCodeIgnoreCase(String code);

    Optional<MenuEntity> findByCodeIgnoreCase(String code);

    List<MenuEntity> findByActiveTrueOrderByDisplayOrderAsc();
}
