package com.lecturemind.commonservice.domain.Response;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ResLoginDTO {
    String accessToken;
    String refreshToken;
    UserLogin userLogin;

    @Data
    @Builder
    @FieldDefaults(level = AccessLevel.PRIVATE)
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UserLogin{
        Long id;
        String email;
        String fullName;
        List<String> roles;
    }
}
