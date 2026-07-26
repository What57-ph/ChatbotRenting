package com.chatbot_renting.notificationservice.dto.response.client;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AllUsersResponse {

    List<UserResponse> allUsers;

    @Data
    public static class UserResponse{
        UUID id;
        String email;
        String fullName;
        String avatarUrl;
        boolean isActive;
        List<String> roles;
        Instant createdAt;
    }

}