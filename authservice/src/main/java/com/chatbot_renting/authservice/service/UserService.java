package com.chatbot_renting.authservice.service;

import com.chatbot_renting.authservice.dto.response.UserResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface UserService {

    Page<UserResponse> getAllUsers(Pageable pageable);

    List<UserResponse> getAllUsers();
}
