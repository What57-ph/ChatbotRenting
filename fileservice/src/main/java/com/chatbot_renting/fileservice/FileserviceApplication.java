package com.chatbot_renting.fileservice;

import jakarta.annotation.PostConstruct;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class FileserviceApplication {

	public static void main(String[] args) {
		SpringApplication.run(FileserviceApplication.class, args);
	}

	@PostConstruct
	public void test() {
		System.out.println("Account Id:"+System.getenv("CLOUDFLARE_R2_ACCOUNT_ID"));
	}

}
