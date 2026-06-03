package com.sigap.msreport.model;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class ContractAccountModel {

    private Long id;

    private String contractNumber;

    private String address;

    private String status;

    private LocalDateTime creationDate;

    private LocalDateTime updateDate;

}
