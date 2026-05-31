package com.lecturemind.commonservice.domain.Request;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SignupRequest {
    String email;
    String password;
    String fullName;
    String avatarUrl;
}
