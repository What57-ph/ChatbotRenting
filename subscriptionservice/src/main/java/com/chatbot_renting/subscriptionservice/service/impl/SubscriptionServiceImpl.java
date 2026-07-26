package com.chatbot_renting.subscriptionservice.service.impl;

import com.chatbot_renting.commonservice.exception.AppBadRequestException;
import com.chatbot_renting.commonservice.exception.AppError;
import com.chatbot_renting.commonservice.exception.AppNotFoundException;
import com.chatbot_renting.subscriptionservice.dto.request.*;
import com.chatbot_renting.subscriptionservice.dto.response.*;
import com.chatbot_renting.subscriptionservice.entity.*;
import com.chatbot_renting.subscriptionservice.entity.enums.*;
import com.chatbot_renting.subscriptionservice.exception.code.SubscriptionErrorCode;
import com.chatbot_renting.subscriptionservice.mapper.*;
import com.chatbot_renting.subscriptionservice.repository.*;
import com.chatbot_renting.subscriptionservice.service.SubscriptionService;
import com.chatbot_renting.subscriptionservice.utils.PlanUtils;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class SubscriptionServiceImpl implements SubscriptionService {

    private static final DateTimeFormatter ORDER_DATE_FMT = DateTimeFormatter.ofPattern("yyyyMMdd");

    private final SubscriptionRepository subscriptionRepository;
    private final SubscriptionPlanRepository planRepository;
    private final OrderRepository orderRepository;
    private final InvoiceRepository invoiceRepository;
    private final UsageSummaryRepository usageSummaryRepository;

    private final SubscriptionMapper subscriptionMapper;
    private final OrderMapper orderMapper;
    private final InvoiceMapper invoiceMapper;
    private final UsageSummaryMapper usageSummaryMapper;
    private final SubscriptionPlanMapper planMapper;
    private final PlanUtils planUtils;

    @Override
    @Transactional
    public SubscriptionCreateResponse createSubscription(UUID userId, SubscriptionCreateRequest request) {
        log.info("Starting createSubscription - userId={}, planId={}, billingCycle={}",
                userId, request.getPlanId(), request.getBillingCycle());
        try {
            List<Subscription> blockingSubs = subscriptionRepository.findByUserIdAndStatusIn(userId,
                    Arrays.asList(SubscriptionStatus.ACTIVE, SubscriptionStatus.TRIALING, SubscriptionStatus.PAUSED));

            if (!blockingSubs.isEmpty()) {
                throw new AppBadRequestException(new AppError(SubscriptionErrorCode.SUBSCRIPTION_ALREADY_EXISTS));
            }

            SubscriptionPlan plan = planRepository.findById(request.getPlanId())
                    .filter(SubscriptionPlan::getActive)
                    .orElseThrow(() -> new AppNotFoundException(new AppError(SubscriptionErrorCode.PLAN_NOT_FOUND)));

            BillingCycle billingCycle = request.getBillingCycle();
            double amount = planUtils.resolvePrice(plan, billingCycle);
            String planSnapshot = planUtils.buildPlanSnapshot(plan, billingCycle, amount);

            boolean hasTrialDays = plan.getTrialDays() > 0;
            long priorSubs = subscriptionRepository.countByUserIdAndStatusNotIn(userId,
                    Arrays.asList(SubscriptionStatus.INCOMPLETE, SubscriptionStatus.INCOMPLETE_EXPIRED));
            boolean isEligibleForTrial = hasTrialDays && (priorSubs == 0);

            List<Subscription> reusableSubs = subscriptionRepository.findByUserIdAndStatusIn(userId,
                    Arrays.asList(SubscriptionStatus.INCOMPLETE, SubscriptionStatus.PAST_DUE, SubscriptionStatus.EXPIRED, SubscriptionStatus.CANCELED, SubscriptionStatus.UNPAID, SubscriptionStatus.INCOMPLETE_EXPIRED));

            Subscription.SubscriptionBuilder subBuilder;
            if (!reusableSubs.isEmpty()) {
                subBuilder = reusableSubs.get(0).toBuilder()
                        .plan(plan)
                        .billingCycle(billingCycle)
                        .autoRenew(true)
                        .cancelledAt(null);
            } else {
                subBuilder = Subscription.builder()
                        .userId(userId)
                        .plan(plan)
                        .billingCycle(billingCycle)
                        .autoRenew(true);
            }

            if (isEligibleForTrial) {
                subBuilder.status(SubscriptionStatus.TRIALING)
                          .currentPeriodStart(LocalDateTime.now())
                          .currentPeriodEnd(LocalDateTime.now().plusDays(plan.getTrialDays()))
                          .currentMaxChatbots(plan.getMaxChatbots())
                          .currentMaxStorageMb(plan.getMaxStorageMb())
                          .currentMaxMonthlyTokens(plan.getMaxMonthlyTokens());
                Subscription subscription = subscriptionRepository.save(subBuilder.build());
                
                log.info("Completed createSubscription (TRIAL) - userId={}, subscriptionId={}", userId, subscription.getId());
                return new SubscriptionCreateResponse(subscriptionMapper.toDto(subscription), null, null);
            }

            subBuilder.status(SubscriptionStatus.INCOMPLETE);
            Subscription subscription = subscriptionRepository.save(subBuilder.build());

            Order order = Order.builder()
                    .subscription(subscription)
                    .userId(userId)
                    .orderType(OrderType.NEW_SUBSCRIPTION)
                    .status(OrderStatus.PENDING)
                    .billingCycle(billingCycle)
                    .amount(amount)
                    .orderNumber(generateOrderNumber("ORD"))
                    .planSnapshot(planSnapshot)
                    .build();
            orderRepository.save(order);

            Invoice invoice = Invoice.builder()
                    .order(order)
                    .amount(amount)
                    .status(InvoiceStatus.UNPAID)
                    .invoiceNumber(generateOrderNumber("INV"))
                    .issuedAt(LocalDateTime.now())
                    .dueDate(LocalDateTime.now().plusDays(1))
                    .planSnapshot(planSnapshot)
                    .build();
            invoiceRepository.save(invoice);

            SubscriptionCreateResponse response = new SubscriptionCreateResponse(
                    subscriptionMapper.toDto(subscription),
                    orderMapper.toDto(order),
                    invoiceMapper.toDto(invoice)
            );
            log.info("Completed createSubscription - userId={}, subscriptionId={}, orderId={}",
                    userId, subscription.getId(), order.getId());
            return response;
        } catch (Exception e) {
            log.error("Error in createSubscription - userId={}, planId={}", userId, request.getPlanId(), e);
            throw e;
        }
    }

    @Override
    public CurrentSubscriptionResponse getCurrentSubscription(UUID userId) {
        log.info("Starting getCurrentSubscription - userId={}", userId);
        try {
            List<Subscription> subs = subscriptionRepository.findByUserIdAndStatusIn(userId,
                    Arrays.asList(SubscriptionStatus.INCOMPLETE, SubscriptionStatus.ACTIVE, SubscriptionStatus.TRIALING, SubscriptionStatus.PAST_DUE));
            if (subs.isEmpty()) {
                throw new AppNotFoundException(new AppError(SubscriptionErrorCode.SUBSCRIPTION_NOT_FOUND));
            }
            Subscription sub = subs.get(0);

            CurrentSubscriptionResponse response = new CurrentSubscriptionResponse();
            response.setId(sub.getId());
            response.setStatus(sub.getStatus().name());
            response.setAutoRenew(sub.getAutoRenew());
            response.setStartDate(sub.getStartDate() != null ? sub.getStartDate().toString() : null);
            response.setCurrentPeriodStart(sub.getCurrentPeriodStart() != null ? sub.getCurrentPeriodStart().toString() : null);
            response.setCurrentPeriodEnd(sub.getCurrentPeriodEnd() != null ? sub.getCurrentPeriodEnd().toString() : null);
            response.setCancelledAt(sub.getCancelledAt() != null ? sub.getCancelledAt().toString() : null);

            SubscriptionPlanDto planDto = planMapper.toDto(sub.getPlan());
            response.setPlan(planDto);

            if (sub.getCurrentPeriodStart() != null) {
                usageSummaryRepository.findBySubscriptionIdAndPeriodStart(sub.getId(), sub.getCurrentPeriodStart())
                        .ifPresent(u -> {
                            UsageSummaryDto uDto = usageSummaryMapper.toDto(u);
                            uDto.setTokensLimit(sub.getPlan().getMaxMonthlyTokens());
                            if (sub.getPlan().getMaxMonthlyTokens() > 0) {
                                uDto.setTokensPercent(uDto.getTokensUsed() * 100.0 / sub.getPlan().getMaxMonthlyTokens());
                            }
                            response.setUsage(uDto);
                        });
            }
            log.info("Completed getCurrentSubscription - userId={}, subscriptionId={}", userId, sub.getId());
            return response;
        } catch (Exception e) {
            log.error("Error in getCurrentSubscription - userId={}", userId, e);
            throw e;
        }
    }

    @Override
    public SubscriptionDto getSubscriptionByUserId(UUID userId) {
        log.info("Starting getSubscriptionByUserId - userId={}", userId);
        try {
            List<Subscription> subs = subscriptionRepository.findByUserIdAndStatusIn(userId,
                    Arrays.asList(SubscriptionStatus.ACTIVE, SubscriptionStatus.TRIALING, SubscriptionStatus.PAST_DUE));
            if (subs.isEmpty()) {
                throw new AppNotFoundException(new AppError(SubscriptionErrorCode.SUBSCRIPTION_NOT_FOUND));
            }
            SubscriptionDto dto = subscriptionMapper.toDto(subs.get(0));
            log.info("Completed getSubscriptionByUserId - userId={}", userId);
            return dto;
        } catch (Exception e) {
            log.error("Error in getSubscriptionByUserId - userId={}", userId, e);
            throw e;
        }
    }

    @Override
    @Transactional
    public SubscriptionDto cancelSubscription(UUID userId) {
        log.info("Starting cancelSubscription - userId={}", userId);
        try {
            List<Subscription> subs = subscriptionRepository.findByUserIdAndStatusIn(userId,
                    Arrays.asList(SubscriptionStatus.ACTIVE, SubscriptionStatus.TRIALING, SubscriptionStatus.PAST_DUE, SubscriptionStatus.INCOMPLETE));
            if (subs.isEmpty()) {
                throw new AppNotFoundException(new AppError(SubscriptionErrorCode.SUBSCRIPTION_NOT_FOUND));
            }
            Subscription sub = subs.getFirst();
            
            if (sub.getStatus() == SubscriptionStatus.INCOMPLETE) {
                sub = sub.toBuilder()
                        .status(SubscriptionStatus.CANCELED)
                        .autoRenew(false)
                        .cancelledAt(LocalDateTime.now())
                        .build();
            } else {
                if (Boolean.FALSE.equals(sub.getAutoRenew())) {
                    throw new AppBadRequestException(new AppError(SubscriptionErrorCode.ALREADY_WAITING_TO_EXPIRED));
                }
                sub = sub.toBuilder()
                        .autoRenew(false)
                        .cancelledAt(LocalDateTime.now())
                        .build();
            }
            subscriptionRepository.save(sub);
            log.info("Completed cancelSubscription - userId={}, subscriptionId={}", userId, sub.getId());
            return subscriptionMapper.toDto(sub);
        } catch (Exception e) {
            log.error("Error in cancelSubscription - userId={}", userId, e);
            throw e;
        }
    }

    @Override
    @Transactional
    public SubscriptionCreateResponse upgradeSubscription(UUID userId, SubscriptionUpgradeRequest request) {
        log.info("Starting upgradeSubscription - userId={}, planId={}, billingCycle={}",
                userId, request.getPlanId(), request.getBillingCycle());
        try {
            List<Subscription> subs = subscriptionRepository.findByUserIdAndStatusIn(userId,
                    Arrays.asList(SubscriptionStatus.ACTIVE, SubscriptionStatus.TRIALING));
            if (subs.isEmpty()) {
                throw new AppBadRequestException(new AppError(SubscriptionErrorCode.SUBSCRIPTION_NOT_ACTIVE));
            }
            Subscription sub = subs.get(0);

            if (sub.getStatus() == SubscriptionStatus.TRIALING) {
                throw new AppBadRequestException(new AppError(SubscriptionErrorCode.TRIAL_CANNOT_BE_CHANGED));
            }

            SubscriptionPlan newPlan = planRepository.findById(request.getPlanId())
                    .filter(SubscriptionPlan::getActive)
                    .orElseThrow(() -> new AppNotFoundException(new AppError(SubscriptionErrorCode.PLAN_NOT_FOUND)));

            if (newPlan.getId().equals(sub.getPlan().getId())) {
                throw new AppBadRequestException(new AppError(SubscriptionErrorCode.SAME_PLAN));
            }

            BillingCycle billingCycle = request.getBillingCycle();
            double currentAmount = planUtils.resolvePrice(sub.getPlan(), billingCycle);
            double newAmount = planUtils.resolvePrice(newPlan, billingCycle);

            if (newAmount < currentAmount) {
                throw new AppBadRequestException(new AppError(SubscriptionErrorCode.USE_DOWNGRADE_ENDPOINT));
            }

            double prorated = newAmount - currentAmount;
            String planSnapshot = planUtils.buildPlanSnapshot(newPlan, billingCycle, prorated);

            sub = sub.toBuilder()
                    .previousPlan(sub.getPlan())
                    .plan(newPlan)
                    .billingCycle(billingCycle)
                    .build();
            subscriptionRepository.save(sub);

            Order order = Order.builder()
                    .subscription(sub)
                    .userId(userId)
                    .orderType(OrderType.UPGRADE)
                    .status(OrderStatus.PENDING)
                    .billingCycle(billingCycle)
                    .amount(prorated)
                    .orderNumber(generateOrderNumber("ORD-UPG"))
                    .planSnapshot(planSnapshot)
                    .build();
            orderRepository.save(order);

            Invoice invoice = Invoice.builder()
                    .order(order)
                    .amount(prorated)
                    .status(InvoiceStatus.UNPAID)
                    .invoiceNumber(generateOrderNumber("INV-UPG"))
                    .issuedAt(LocalDateTime.now())
                    .dueDate(LocalDateTime.now().plusDays(1))
                    .planSnapshot(planSnapshot)
                    .build();
            invoiceRepository.save(invoice);

            SubscriptionCreateResponse response = new SubscriptionCreateResponse(
                    subscriptionMapper.toDto(sub), orderMapper.toDto(order), invoiceMapper.toDto(invoice));
            log.info("Completed upgradeSubscription - userId={}, subscriptionId={}, orderId={}",
                    userId, sub.getId(), order.getId());
            return response;
        } catch (Exception e) {
            log.error("Error in upgradeSubscription - userId={}, planId={}", userId, request.getPlanId(), e);
            throw e;
        }
    }

    @Override
    @Transactional
    public SubscriptionDto downgradeSubscription(UUID userId, SubscriptionDowngradeRequest request) {
        log.info("Starting downgradeSubscription - userId={}, planId={}", userId, request.getPlanId());
        try {
            List<Subscription> subs = subscriptionRepository.findByUserIdAndStatusIn(userId,
                    Arrays.asList(SubscriptionStatus.ACTIVE, SubscriptionStatus.TRIALING));
            if (subs.isEmpty()) {
                throw new AppBadRequestException(new AppError(SubscriptionErrorCode.SUBSCRIPTION_NOT_ACTIVE));
            }
            Subscription sub = subs.get(0);
            
            if (sub.getStatus() == SubscriptionStatus.TRIALING) {
                throw new AppBadRequestException(new AppError(SubscriptionErrorCode.TRIAL_CANNOT_BE_CHANGED));
            }
            
            SubscriptionPlan newPlan = planRepository.findById(request.getPlanId())
                    .filter(SubscriptionPlan::getActive)
                    .orElseThrow(() -> new AppNotFoundException(new AppError(SubscriptionErrorCode.PLAN_NOT_FOUND)));

            if (newPlan.getId().equals(sub.getPlan().getId())) {
                throw new AppBadRequestException(new AppError(SubscriptionErrorCode.SAME_PLAN));
            }

            sub = sub.toBuilder()
                    .scheduledPlanId(newPlan.getId())
                    .build();
            subscriptionRepository.save(sub);
            log.info("Completed downgradeSubscription - userId={}, subscriptionId={}, scheduledPlanId={}",
                    userId, sub.getId(), newPlan.getId());
            return subscriptionMapper.toDto(sub);
        } catch (Exception e) {
            log.error("Error in downgradeSubscription - userId={}, planId={}", userId, request.getPlanId(), e);
            throw e;
        }
    }

    @Override
    @Transactional
    public SubscriptionDto toggleAutoRenew(UUID userId, SubscriptionAutoRenewRequest request) {
        log.info("Starting toggleAutoRenew - userId={}, autoRenew={}", userId, request.getAutoRenew());
        try {

            List<Subscription> subs = subscriptionRepository.findByUserIdAndStatusIn(userId,
                    Arrays.asList(SubscriptionStatus.ACTIVE, SubscriptionStatus.TRIALING));

            if (subs == null || subs.isEmpty()) {
                throw new AppNotFoundException(new AppError(SubscriptionErrorCode.SUBSCRIPTION_NOT_FOUND));
            }

            Subscription sub = subs.getFirst();

            sub.setAutoRenew(request.getAutoRenew());
            if (request.getAutoRenew()) {
                sub.setCancelledAt(null);
            } else {
                sub.setCancelledAt(LocalDateTime.now());
            }

            Subscription updatedSub = subscriptionRepository.save(sub);

            log.info("Completed toggleAutoRenew - userId={}, subscriptionId={}", userId, updatedSub.getId());
            return subscriptionMapper.toDto(updatedSub);

        } catch (AppNotFoundException e) {
            log.warn("Business warning in toggleAutoRenew - userId={}: {}", userId, e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Unexpected error in toggleAutoRenew - userId={}", userId, e);
            throw e;
        }
    }


    private String generateOrderNumber(String prefix) {
        return prefix + "-" + LocalDateTime.now().format(ORDER_DATE_FMT) + "-"
                + UUID.randomUUID().toString().substring(0, 6).toUpperCase();
    }
}
