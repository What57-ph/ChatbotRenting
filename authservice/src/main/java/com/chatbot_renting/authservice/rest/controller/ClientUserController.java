package com.chatbot_renting.authservice.rest.controller;

import com.chatbot_renting.authservice.dto.response.UserResponse;
import com.chatbot_renting.authservice.rest.api.ClientUserApi;
import com.chatbot_renting.authservice.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
public class ClientUserController implements ClientUserApi {

    private final UserService userService;

    @Override
    public ResponseEntity<Page<UserResponse>> getAllUsers(Integer page, Integer size) {
        Pageable pageable;
        if (page == null && size == null) {
            log.info("CLIENT-API request to get ALL users (no pagination)");
            pageable = PageRequest.of(0, Integer.MAX_VALUE);
        } else {
            int pageNum = (page != null) ? page : 0;
            int pageSize = (size != null) ? size : 10;
            log.info("CLIENT-API request to get users: page={}, size={}", pageNum, pageSize);
            pageable = PageRequest.of(pageNum, pageSize);
        }

        return ResponseEntity.ok(userService.getAllUsers(pageable));
    }
}
