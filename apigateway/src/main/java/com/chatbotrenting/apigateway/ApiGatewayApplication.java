package com.chatbotrenting.apigateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication(scanBasePackages = {
		"com.chatbotrenting.apigateway",
		"com.lecturemind.commonservice"
})
@EnableDiscoveryClient
@EnableFeignClients(basePackages = "com.chatbotrenting.apigateway.client")
public class ApiGatewayApplication {

	public static void main(String[] args) {
		SpringApplication.run(ApiGatewayApplication.class, args);
	}

}
