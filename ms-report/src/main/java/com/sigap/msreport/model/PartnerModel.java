package com.sigap.msreport.model;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class PartnerModel {

    private Long idPartner;

    private String taxIdentification;

    private String names;

    private String lastName;

    private String address;

    private String phone;

    private String email;

    private Boolean status = true;

    private LocalDateTime registrationDate;

    private LocalDateTime updateDate;
}
