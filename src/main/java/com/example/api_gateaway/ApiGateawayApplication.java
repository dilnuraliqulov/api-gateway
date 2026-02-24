package com.example.api_gateaway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class ApiGateawayApplication {

	public static void main(String[] args) {

		System.out.println("Starting API Gateway...");
		SpringApplication.run(ApiGateawayApplication.class, args);

	}

}
