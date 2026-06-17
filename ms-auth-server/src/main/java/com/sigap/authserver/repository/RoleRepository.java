package com.sigap.authserver.repository;

import com.sigap.authserver.infrastructure.entity.RoleEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<RoleEntity, Long> {
}
