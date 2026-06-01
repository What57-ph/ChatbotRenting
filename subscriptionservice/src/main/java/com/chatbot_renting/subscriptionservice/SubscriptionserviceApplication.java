package com.chatbot_renting.subscriptionservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class SubscriptionserviceApplication {

	public static void main(String[] args) {
		SpringApplication.run(SubscriptionserviceApplication.class, args);
	}

}
