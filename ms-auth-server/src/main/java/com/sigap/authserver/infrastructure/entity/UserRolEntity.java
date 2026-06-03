package com.sigap.authserver.infrastructure.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "usuario_roles")
public class UserRolEntity {

    @EmbeddedId
    private UserRolId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("idUser")
    @JoinColumn(name = "id_usuario", nullable = false)
    private UserEntity user;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("idRol")
    @JoinColumn(name = "id_rol", nullable = false)
    private RolEntity rol;

    @Column(name = "fecha_asignacion", nullable = false, updatable = false)
    private LocalDateTime assignmentDate;

    public static UserRolEntity of(UserEntity usuario, RolEntity rol) {
        UserRolEntity entity = new UserRolEntity();
        entity.setUser(usuario);
        entity.setRol(rol);
        return entity;
    }

    @PrePersist
    void prePersist() {
        if (assignmentDate == null) {
            assignmentDate = LocalDateTime.now();
        }
    }
}
