package com.sigap.authserver.config;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "app.bootstrap.admin")
public class BootstrapAdminProperties {

    private boolean enabled = true;

    @NotBlank
    private String username = "admin";

    @NotBlank
    private String password = "Admin12345*";

    @Email
    private String email = "admin@sigap.local";

    @NotBlank
    private String nombres = "Administrador SIGAP";
}
