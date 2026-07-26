package com.chatbot_renting.authservice.rest.api;

import com.chatbot_renting.authservice.dto.response.UserResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@RequestMapping("/service-api/v1/users")
public interface ServiceUserApi {

    @GetMapping
    ResponseEntity<List<UserResponse>> getAllUsers();
}
