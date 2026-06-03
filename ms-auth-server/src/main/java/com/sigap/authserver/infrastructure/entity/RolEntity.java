package com.sigap.authserver.infrastructure.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "roles")
public class RolEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_rol")
    private Long idRol;

    @Column(name = "nombre", nullable = false, unique = true, length = 100)
    private String rolName;

    @Column(name = "descripcion", length = 150)
    private String descriptionRol;

    @Column(name = "estado", nullable = false)
    private Boolean statusRol = true;

    @Column(name = "fecha_creacion", nullable = false, updatable = false)
    private LocalDateTime creationDateRol;

    @OneToMany(mappedBy = "rol", fetch = FetchType.LAZY)
    private Set<UserRolEntity> usersRol = new HashSet<>();

    @PrePersist
    void prePersist() {
        if (creationDateRol == null) {
            creationDateRol = LocalDateTime.now();
        }
        if (statusRol == null) {
            statusRol = true;
        }
    }

}
