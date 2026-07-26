package com.chatbot_renting.subscriptionservice.dto.request;

import com.chatbot_renting.subscriptionservice.entity.enums.UsageType;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;
import lombok.Data;

@Data
public class UsageCheckRequest {
    @NotNull
    private UUID userId;
    
    @NotNull
    private UsageType usageType;
}
