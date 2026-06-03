package com.sigap.msreport;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class MsReportApplication {

	public static void main(String[] args) {
		SpringApplication.run(MsReportApplication.class, args);
	}

}
