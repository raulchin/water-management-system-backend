package com.sigap.authserver.infrastructure.entity;


import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "rol_menus")
public class RoleMenuEntity {

    @EmbeddedId
    private RoleMenuId id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @MapsId("roleId")
    @JoinColumn(name = "id_rol", nullable = false)
    private RoleEntity role;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @MapsId("menuId")
    @JoinColumn(name = "id_menu", nullable = false)
    private MenuEntity menu;

    @Column(name = "fecha_creacion", nullable = false)
    private LocalDateTime createdAt;

    @PrePersist
    public void prePersist() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
    }
}
