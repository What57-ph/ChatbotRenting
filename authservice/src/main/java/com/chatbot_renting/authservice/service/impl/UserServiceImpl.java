package com.chatbot_renting.authservice.service.impl;

import com.chatbot_renting.authservice.dto.response.UserResponse;
import com.chatbot_renting.authservice.entity.Role;
import com.chatbot_renting.authservice.entity.User;
import com.chatbot_renting.authservice.repository.UserRepository;
import com.chatbot_renting.authservice.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    public Page<UserResponse> getAllUsers(Pageable pageable) {
        log.info("Fetching users with pagination: page={}, size={}", pageable.getPageNumber(), pageable.getPageSize());

        Page<UserResponse> result = userRepository.findAll(pageable)
                .map(this::toUserResponse);

        log.info("Found {} users (total: {})", result.getNumberOfElements(), result.getTotalElements());
        return result;
    }

    @Override
    public List<UserResponse> getAllUsers() {
        log.info("Fetching all users (no pagination)");

        List<UserResponse> result = userRepository.findAll().stream()
                .map(this::toUserResponse)
                .toList();

        log.info("Found {} users", result.size());
        return result;
    }

    private UserResponse toUserResponse(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .fullName(user.getFullName())
                .avatarUrl(user.getAvatarUrl())
                .isActive(user.isActive())
                .roles(
                        user.getRoles() == null
                                ? List.of()
                                : user.getRoles().stream()
                                .map(Role::getName)
                                .toList()
                )
                .createdAt(user.getCreatedAt())
                .build();
    }
}
