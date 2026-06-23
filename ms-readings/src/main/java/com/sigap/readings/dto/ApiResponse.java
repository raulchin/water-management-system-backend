package com.sigap.readings.dto;

public record ApiResponse<T>(
        String codResult,
        String message,
        T data
) {

    public static <T> ApiResponse<T> success(String message, T data) {
        return new ApiResponse<>("000", message, data);
    }

    public static <T> ApiResponse<T> error(String message, T data) {
        return new ApiResponse<>("999", message, data);
    }
}
