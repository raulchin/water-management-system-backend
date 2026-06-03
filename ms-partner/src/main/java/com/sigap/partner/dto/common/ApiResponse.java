package com.sigap.partner.dto.common;

public record ApiResponse<T>(
        String codResult,
        String message,
        T data
) {
    public static <T> ApiResponse<T> ok(T data) {
        return new ApiResponse<>("000", "OK", data);
    }

    public static <T> ApiResponse<T> ok(String message, T data) {
        return new ApiResponse<>("000", message, data);
    }

    public static <T> ApiResponse<T> error(String message) {
        return new ApiResponse<>("999", message, null);
    }
}
