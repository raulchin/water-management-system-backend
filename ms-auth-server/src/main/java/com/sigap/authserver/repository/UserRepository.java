package com.sigap.authserver.repository;

import com.sigap.authserver.infrastructure.entity.UserEntity;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UserRepository extends JpaRepository<UserEntity, Long> {

    Optional<UserEntity> findByUsername(String username);

    boolean existsByUsernameIgnoreCase(String username);

    boolean existsByEmailIgnoreCase(String email);

    @EntityGraph(attributePaths = {"usuariosRol", "usuariosRol.rol"})
    Optional<UserEntity> findWithRolesByUsername(String username);

    @Query("""
            select distinct u
                    from UserEntity u
                    left join fetch u.usuariosRol ur
                    left join fetch ur.rol
                    where lower(u.username) = lower(:username)
            """)
    Optional<UserEntity> findByUsernameWithRoles(@Param("username") String username);
}
