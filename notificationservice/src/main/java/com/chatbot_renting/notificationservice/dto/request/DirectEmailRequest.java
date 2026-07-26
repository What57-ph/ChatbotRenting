package com.chatbot_renting.notificationservice.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DirectEmailRequest {

    @NotBlank
    @Email
    private String email;

    @NotBlank
    private String templateCode;

    private Map<String, Object> contextData;
}
