package com.chatbot_renting.subscriptionservice.dto.response;

import lombok.Data;

@Data
public class UsageSummaryDto {
    private Long tokensUsed;
    private Integer tokensLimit;
    private Double tokensPercent;
    
    private Double storageUsedMb;
    private Integer storageLimitMb;
    private Double storagePercent;
    
    private Integer chatbotCount;
    private Integer chatbotLimit;
    private Integer filesCount;
    
    private Long apiCalls;
}
