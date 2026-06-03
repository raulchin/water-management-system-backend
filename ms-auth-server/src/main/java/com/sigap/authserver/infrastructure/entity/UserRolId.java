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
public class UserRolId implements Serializable {

    private static final long serialVersionUID = 1L;

    @Column(name = "id_usuario")
    private Long idUser;

    @Column(name = "id_rol")
    private Long idRol;
}
