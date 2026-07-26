package com.chatbot_renting.subscriptionservice.dto.request;

import com.chatbot_renting.subscriptionservice.entity.enums.UsageType;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;
import lombok.Data;

@Data
public class UsageRecordRequest {
    @NotNull
    private UUID userId;
    
    private String botId;
    
    @NotNull
    private UsageType usageType;
    
    @NotNull
    private Integer quantity;
}
