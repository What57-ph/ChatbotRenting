package com.chatbot_renting.authservice.rest.controller;

import com.chatbot_renting.authservice.dto.response.UserResponse;
import com.chatbot_renting.authservice.rest.api.ServiceUserApi;
import com.chatbot_renting.authservice.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
public class ServiceUserController implements ServiceUserApi {

    private final UserService userService;

    @Override
    public ResponseEntity<List<UserResponse>> getAllUsers() {
        log.info("SERVICE-API request to get ALL users");
        return ResponseEntity.ok(userService.getAllUsers());
    }
}
