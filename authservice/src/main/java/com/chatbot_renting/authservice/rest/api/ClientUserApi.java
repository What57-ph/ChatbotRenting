package com.chatbot_renting.authservice.rest.api;

import com.chatbot_renting.authservice.dto.response.UserResponse;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@RequestMapping("/client-api/v1/users")
public interface ClientUserApi {

    @GetMapping
    ResponseEntity<Page<UserResponse>> getAllUsers(
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size
    );
}