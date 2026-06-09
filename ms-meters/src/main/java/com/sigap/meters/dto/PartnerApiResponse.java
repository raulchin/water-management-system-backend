package com.sigap.meters.dto;

public record PartnerApiResponse<T>(

        String codResult,
        String message,
        T data

) {
}