package com.zwz.gateway.service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

@EnableFeignClients(basePackages = {"com.zwz.gateway.service"})
@EnableDiscoveryClient
@SpringBootApplication
public class ZwzGatewayServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(ZwzGatewayServiceApplication.class, args);
	}

}
