package com.sigap.partner.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "socios")
public class PartnerEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "socio_id")
    private Long idPartner;

    @Column(name = "cedula_ruc", nullable = false, unique = true, length = 20)
    private String taxIdentification;

    @Column( name = "nombres", nullable = false, length = 100)
    private String names;

    @Column (name = "apellidos", nullable = false, length = 100 )
    private String lastName;

    @Column( name = "direccion", columnDefinition = "TEXT" )
    private String address;

    @Column( name = "telefono", nullable = false, length = 20 )
    private String phone;

    private String email;

    @Column( name = "estado" )
    private Boolean status = true;

    @Column(name = "fecha_ingreso", insertable = false, updatable = false)
    private LocalDateTime registrationDate;

    @Column(name = "fecha_actualizacion")
    private LocalDateTime updateDate;

    @OneToMany(
            mappedBy = "socio",
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            fetch = FetchType.LAZY
    )

    private List<ContractAccountEntity> contractAccounts = new ArrayList<>();

    @PrePersist
    public void prePersist() {
        this.registrationDate = LocalDateTime.now();
        if (this.status == null) {
            this.status = true;
        }
    }

    @PreUpdate
    public void preUpdate() {
        this.updateDate = LocalDateTime.now();
    }

}
