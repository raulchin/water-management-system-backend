package com.sigap.meters.dto;

public record PartnerResponse(

        Long idPartner,

        String taxIdentification,

        String names,

        String lastName,

        String address,

        String phone,

        String email,

        Boolean status

) {
}
