package com.chatbotrenting.apigateway.client;

import org.springframework.cloud.openfeign.FeignClient;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "auth-service", url = "http://localhost:9000/api/v1/auth")
public interface AuthClient {

        @PostMapping(value = "/validate-token", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
        ResponseEntity<Long> validateToken(@RequestParam String token);
}
