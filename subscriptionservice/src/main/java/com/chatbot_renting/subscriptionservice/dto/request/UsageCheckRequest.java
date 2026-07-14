package com.chatbot_renting.subscriptionservice.dto.request;

import com.chatbot_renting.subscriptionservice.entity.enums.UsageType;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UsageCheckRequest {
    @NotNull
    private Long userId;
    
    @NotNull
    private UsageType usageType;
}
