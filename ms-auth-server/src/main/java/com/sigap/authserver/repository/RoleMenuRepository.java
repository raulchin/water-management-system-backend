package com.sigap.authserver.repository;

import com.sigap.authserver.infrastructure.entity.RoleMenuEntity;
import com.sigap.authserver.infrastructure.entity.RoleMenuId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RoleMenuRepository extends JpaRepository<RoleMenuEntity, RoleMenuId> {

    List<RoleMenuEntity> findById_RoleId(Long roleId);

    void deleteById_RoleId(Long roleId);

}
