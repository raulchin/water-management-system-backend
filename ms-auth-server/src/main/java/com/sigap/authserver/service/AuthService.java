package com.sigap.authserver.service;

import com.sigap.authserver.dto.auth.LoginRequest;
import com.sigap.authserver.dto.auth.LoginResponse;
import com.sigap.authserver.dto.auth.RegisterRequest;
import com.sigap.authserver.helper.JwtHelper;
import com.sigap.authserver.infrastructure.entity.RoleEntity;
import com.sigap.authserver.infrastructure.entity.UserEntity;
import com.sigap.authserver.repository.RolRepository;
import com.sigap.authserver.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;


import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private static final int MAX_INTENTOS_FALLIDOS = 5;

    private static final String ROL_USER = "USER";

    private final UserRepository userRepository;

    private final RolRepository rolRepository;

    private final JwtHelper jwtService;

    private final PasswordEncoder passwordEncoder;

    @Transactional
    public LoginResponse register(RegisterRequest request){

        log.info("Registrar un usuario...");

        validarDuplicados(request.username(), request.email());
        RoleEntity rolEntity = rolRepository.findByRolNameIgnoreCase(request.rol())
                .orElseThrow(() -> new IllegalStateException("No existe el rol USER en la base de datos."));

        UserEntity user = new UserEntity();
        user.setUsername(request.username());
        user.setPasswordHash(passwordEncoder.encode(request.password()));
        user.setEmail(request.email().trim().toLowerCase());
        user.setStatusUser(Boolean.TRUE);
        user.setBlocked(Boolean.FALSE);
        user.setFailedAttempts(0);
        user.addRol(rolEntity);
        user.setNames(request.nombres());
        UserEntity userSaved = userRepository.save(user);
        return buildLoginResponse(userSaved);

    }

    @Transactional
    public LoginResponse login (LoginRequest request){

        UserEntity usuario = userRepository.findByUsernameWithRoles(request.username())
                .orElseThrow(() -> new BadCredentialsException("Credenciales inválidas."));

        if (!passwordEncoder.matches(request.password(), usuario.getPasswordHash())) {
            registrarIntentoFallido(usuario);
            throw new BadCredentialsException("Credenciales inválidas.");
        }

        if (!Boolean.TRUE.equals(usuario.getStatusUser())) {
            throw new IllegalStateException("El usuario se encuentra inactivo.");
        }

        if (Boolean.TRUE.equals(usuario.getBlocked())) {
            throw new IllegalStateException("El usuario se encuentra bloqueado.");
        }

        usuario.setFailedAttempts(0);
        UserEntity usuarioActualizado = userRepository.save(usuario);
        return buildLoginResponse(usuarioActualizado);
    }


    private void validarDuplicados(String username, String email) {
        if (userRepository.existsByUsernameIgnoreCase(username)) {
            throw new IllegalArgumentException("El username ya se encuentra registrado.");
        }
        if (userRepository.existsByEmailIgnoreCase(email)) {
            throw new IllegalArgumentException("El correo ya se encuentra registrado.");
        }
    }

    private LoginResponse buildLoginResponse(UserEntity usuario) {
        List<String> roles = extraerRoles(usuario);
        Long roleId = extraerRoleId(usuario);
        String token = jwtService.generateToken(usuario.getIdUser(), usuario.getUsername(), roles);
        log.info("Token generado correctamente para usuario: {}", usuario.getUsername());
        return new LoginResponse(
                token,
                "Bearer",
                jwtService.getExpirationMinutes(),
                usuario.getIdUser(),
                usuario.getUsername(),
                usuario.getEmail(),
                usuario.getNames(),
                roleId,
                roles
        );
    }

    private List<String> extraerRoles(UserEntity userEntity) {
        return userEntity.getUsuariosRol().stream()
                .map(usuarioRol -> usuarioRol.getRol().getRolName())
                .map(String::toUpperCase)
                .distinct()
                .toList();
    }

    private void registrarIntentoFallido(UserEntity usuario) {
        int intentos = usuario.getFailedAttempts() == null ? 0 : usuario.getFailedAttempts();
        int nuevosIntentos = intentos + 1;
        usuario.setFailedAttempts(nuevosIntentos);
        if (nuevosIntentos >= MAX_INTENTOS_FALLIDOS) {
            usuario.setBlocked(Boolean.TRUE);
        }
        userRepository.save(usuario);
    }

    private Long extraerRoleId(UserEntity userEntity) {
        return userEntity.getUsuariosRol().stream()
                .map(usuarioRol -> usuarioRol.getRol().getIdRol())
                .findFirst()
                .orElse(null);
    }
}
