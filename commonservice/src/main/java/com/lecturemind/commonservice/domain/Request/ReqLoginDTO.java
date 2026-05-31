package com.lecturemind.commonservice.domain.Request;

import jakarta.validation.constraints.NotBlank;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ReqLoginDTO {
    @NotBlank(message = "Email cannot be blank")
    String email;
    @NotBlank(message = "Password cannot be blank")
    String password;
}
