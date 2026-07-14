package com.chatbot_renting.subscriptionservice.service.impl;

import com.chatbot_renting.subscriptionservice.dto.request.UsageCheckRequest;
import com.chatbot_renting.subscriptionservice.dto.request.UsageRecordRequest;
import com.chatbot_renting.subscriptionservice.dto.response.UsageCheckResponse;
import com.chatbot_renting.subscriptionservice.entity.Subscription;
import com.chatbot_renting.subscriptionservice.entity.UsageRecord;
import com.chatbot_renting.subscriptionservice.entity.UsageSummary;
import com.chatbot_renting.subscriptionservice.entity.enums.SubscriptionStatus;
import com.chatbot_renting.subscriptionservice.exception.code.SubscriptionErrorCode;
import com.chatbot_renting.subscriptionservice.repository.SubscriptionRepository;
import com.chatbot_renting.subscriptionservice.repository.UsageRecordRepository;
import com.chatbot_renting.subscriptionservice.repository.UsageSummaryRepository;
import com.chatbot_renting.subscriptionservice.service.UsageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class UsageServiceImpl implements UsageService {

    private final SubscriptionRepository subscriptionRepository;
    private final UsageRecordRepository usageRecordRepository;
    private final UsageSummaryRepository usageSummaryRepository;

    @Override
    @Transactional
    public void recordUsage(UsageRecordRequest request) {
        log.info("Starting recordUsage - userId={}, usageType={}, quantity={}",
                request.getUserId(), request.getUsageType(), request.getQuantity());
        try {
            List<Subscription> subs = subscriptionRepository.findByUserIdAndStatusIn(request.getUserId(),
                    Arrays.asList(SubscriptionStatus.ACTIVE, SubscriptionStatus.TRIALING));
            if (subs.isEmpty()) {
                log.info("Completed recordUsage - no active subscription for userId={}", request.getUserId());
                return;
            }

            Subscription sub = subs.get(0);

            UsageRecord record = UsageRecord.builder()
                    .subscription(sub)
                    .userId(request.getUserId())
                    .botId(request.getBotId())
                    .usageType(request.getUsageType())
                    .quantity(request.getQuantity())
                    .build();
            usageRecordRepository.save(record);

            if (sub.getCurrentPeriodStart() != null && sub.getCurrentPeriodEnd() != null) {
                UsageSummary existing = usageSummaryRepository
                        .findBySubscriptionIdAndPeriodStart(sub.getId(), sub.getCurrentPeriodStart())
                        .orElseGet(() -> UsageSummary.builder()
                                .subscription(sub)
                                .periodStart(sub.getCurrentPeriodStart())
                                .periodEnd(sub.getCurrentPeriodEnd())
                                .tokensUsed(0L)
                                .storageUsedMb(0.0)
                                .chatbotCount(0)
                                .filesCount(0)
                                .apiCalls(0L)
                                .build());

                UsageSummary updated = applyUsageDelta(existing, request);
                usageSummaryRepository.save(updated);
            }
            log.info("Completed recordUsage - userId={}, subscriptionId={}", request.getUserId(), sub.getId());
        } catch (Exception e) {
            log.error("Error in recordUsage - userId={}", request.getUserId(), e);
            throw e;
        }
    }

    @Override
    public UsageCheckResponse checkQuota(UsageCheckRequest request) {
        log.info("Starting checkQuota - userId={}, usageType={}", request.getUserId(), request.getUsageType());
        try {
            List<Subscription> subs = subscriptionRepository.findByUserIdAndStatusIn(request.getUserId(),
                    Arrays.asList(SubscriptionStatus.ACTIVE, SubscriptionStatus.TRIALING));
            if (subs.isEmpty()) {
                log.info("Completed checkQuota - no active subscription for userId={}", request.getUserId());
                return UsageCheckResponse.builder().allowed(false).reason("NO_ACTIVE_SUBSCRIPTION").build();
            }
            Subscription sub = subs.get(0);

            UsageCheckResponse response = UsageCheckResponse.builder().allowed(true).build();
            if (sub.getCurrentPeriodStart() != null) {
                usageSummaryRepository.findBySubscriptionIdAndPeriodStart(sub.getId(), sub.getCurrentPeriodStart())
                        .ifPresent(summary -> applyQuotaCheck(sub, summary, request, response));
            }
            log.info("Completed checkQuota - userId={}, allowed={}", request.getUserId(), response.getAllowed());
            return response;
        } catch (Exception e) {
            log.error("Error in checkQuota - userId={}", request.getUserId(), e);
            throw e;
        }
    }

    /**
     * Returns a new UsageSummary with the delta applied using toBuilder().
     * No setter calls on the original entity.
     */
    private UsageSummary applyUsageDelta(UsageSummary summary, UsageRecordRequest request) {
        return switch (request.getUsageType()) {
            case TOKEN_USED -> summary.toBuilder()
                    .tokensUsed(summary.getTokensUsed() + request.getQuantity())
                    .build();
            case API_CALL -> summary.toBuilder()
                    .apiCalls(summary.getApiCalls() + request.getQuantity())
                    .build();
            case CHATBOT_CREATED -> summary.toBuilder()
                    .chatbotCount(summary.getChatbotCount() + request.getQuantity())
                    .build();
            case CHATBOT_DELETED -> summary.toBuilder()
                    .chatbotCount(summary.getChatbotCount() - request.getQuantity())
                    .build();
            case FILE_UPLOADED -> summary.toBuilder()
                    .storageUsedMb(summary.getStorageUsedMb() + request.getQuantity())
                    .filesCount(summary.getFilesCount() + 1)
                    .build();
            case FILE_DELETED -> summary.toBuilder()
                    .storageUsedMb(summary.getStorageUsedMb() - request.getQuantity())
                    .filesCount(summary.getFilesCount() - 1)
                    .build();
        };
    }

    private void applyQuotaCheck(
            Subscription sub,
            UsageSummary summary,
            UsageCheckRequest request,
            UsageCheckResponse response
    ) {
        switch (request.getUsageType()) {
            case TOKEN_USED -> {
                long limit = sub.getCurrentMaxMonthlyTokens() != null ? sub.getCurrentMaxMonthlyTokens() : sub.getPlan().getMaxMonthlyTokens();
                response.setLimit(limit);
                response.setCurrent(summary.getTokensUsed());
                if (summary.getTokensUsed() >= limit) {
                    response.setAllowed(false);
                    response.setReason("TOKEN_LIMIT_EXCEEDED");
                }
            }
            case CHATBOT_CREATED -> {
                long limit = sub.getCurrentMaxChatbots() != null ? sub.getCurrentMaxChatbots() : sub.getPlan().getMaxChatbots();
                response.setLimit(limit);
                response.setCurrent((long) summary.getChatbotCount());
                if (summary.getChatbotCount() >= limit) {
                    response.setAllowed(false);
                    response.setReason("CHATBOT_LIMIT_EXCEEDED");
                }
            }
            default -> {
                // no quota rule for other types
            }
        }
    }
}
