package com.sigap.authserver.infrastructure.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@Embeddable
public class UserRolId {

    @Column(name = "id_usuario")
    private Long idUser;

    @Column(name = "id_rol")
    private Long idRol;
}
