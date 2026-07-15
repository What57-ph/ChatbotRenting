package com.chatbot_renting.notificationservice.dto.request;

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
public class DirectZaloRequest {

    @NotBlank
    private String zaloIdentifier;

    @NotBlank
    private String zaloTemplateId;

    private Map<String, Object> contextData;
}
