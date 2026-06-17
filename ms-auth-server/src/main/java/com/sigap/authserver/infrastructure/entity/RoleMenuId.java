package com.sigap.authserver.infrastructure.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.*;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@Embeddable
public class RoleMenuId implements Serializable {

    @Column(name = "id_rol")
    private Long roleId;

    @Column(name = "id_menu")
    private Long menuId;
}
