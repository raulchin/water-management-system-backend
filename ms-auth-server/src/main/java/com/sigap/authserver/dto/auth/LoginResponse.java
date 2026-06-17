package com.sigap.authserver.dto.auth;

import java.util.List;

public record LoginResponse(

        String accessToken,

        String tokenType,

        long expiresInMinutes,

        Long idUsuario,

        String username,

        String email,

        String nombres,

        Long roleId,

        List<String> roles
) {
}
