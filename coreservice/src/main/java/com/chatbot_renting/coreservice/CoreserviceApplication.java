package com.chatbot_renting.coreservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication(scanBasePackages = {"com.chatbot_renting.coreservice", "com.chatbot_renting.commonservice"})
@EnableDiscoveryClient
public class CoreserviceApplication {

	public static void main(String[] args) {
		SpringApplication.run(CoreserviceApplication.class, args);
	}

}
