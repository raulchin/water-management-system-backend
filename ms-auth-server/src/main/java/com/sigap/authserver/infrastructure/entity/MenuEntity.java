package com.sigap.authserver.infrastructure.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "menus")
public class MenuEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_menu")
    private Long menuId;

    @Column(name = "nombre", nullable = false, length = 100)
    private String name;

    @Column(name = "descripcion", length = 200)
    private String description;

    @Column(name = "ruta", length = 200)
    private String path;

    @Column(name = "icono", length = 100)
    private String icon;

    @Column(name = "codigo", nullable = false, unique = true, length = 100)
    private String code;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_menu_padre")
    private MenuEntity parent;

    @OneToMany(mappedBy = "parent", fetch = FetchType.LAZY)
    @Builder.Default
    private Set<MenuEntity> children = new HashSet<>();

    @Column(name = "orden", nullable = false)
    private Integer displayOrder;

    @Column(name = "estado", nullable = false)
    private Boolean active;

    @Column(name = "fecha_creacion", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "fecha_actualizacion")
    private LocalDateTime updatedAt;

    @PrePersist
    public void prePersist() {
        if (active == null) {
            active = true;
        }

        if (displayOrder == null) {
            displayOrder = 0;
        }

        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
    }

    @PreUpdate
    public void preUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
