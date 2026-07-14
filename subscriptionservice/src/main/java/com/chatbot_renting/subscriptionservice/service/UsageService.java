package com.chatbot_renting.subscriptionservice.service;

import com.chatbot_renting.subscriptionservice.dto.request.UsageCheckRequest;
import com.chatbot_renting.subscriptionservice.dto.request.UsageRecordRequest;
import com.chatbot_renting.subscriptionservice.dto.response.UsageCheckResponse;

public interface UsageService {
    void recordUsage(UsageRecordRequest request);
    UsageCheckResponse checkQuota(UsageCheckRequest request);
}
