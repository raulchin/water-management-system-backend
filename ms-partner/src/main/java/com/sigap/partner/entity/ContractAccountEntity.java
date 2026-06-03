package com.sigap.partner.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "cuentas_contrato")
public class ContractAccountEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "cuenta_id")
    private Long id;

    @Column(name = "numero_contrato", nullable = false, unique = true, length = 30)
    private String contractNumber;

    @Column(name = "direccion", nullable = false, length = 250)
    private String address;

    @Column(name = "estado", nullable = false, length = 20)
    private String status = "ACTIVA";

    @Column(name = "fecha_creacion", nullable = false, updatable = false)
    private LocalDateTime creationDate;

    @Column(name = "fecha_actualizacion")
    private LocalDateTime updateDate;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "socio_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_cuentas_contrato_socio")
    )
    private PartnerEntity socio;

    @PrePersist
    public void prePersist() {
        this.creationDate = LocalDateTime.now();
        if (this.status == null) {
            this.status = "ACTIVA";
        }
    }

    @PreUpdate
    public void preUpdate() {
        this.updateDate = LocalDateTime.now();
    }
}

