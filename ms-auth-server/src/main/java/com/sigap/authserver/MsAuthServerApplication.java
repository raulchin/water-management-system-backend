package com.sigap.authserver;

import com.sigap.authserver.config.BootstrapAdminProperties;
import com.sigap.authserver.config.JwtProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@SpringBootApplication
@EnableDiscoveryClient
@EnableConfigurationProperties({JwtProperties.class, BootstrapAdminProperties.class})
public class MsAuthServerApplication {

	public static void main(String[] args) {
		SpringApplication.run(MsAuthServerApplication.class, args);
	}

}
