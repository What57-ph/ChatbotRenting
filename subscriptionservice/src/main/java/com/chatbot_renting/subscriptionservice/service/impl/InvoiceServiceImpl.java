package com.chatbot_renting.subscriptionservice.service.impl;

import com.chatbot_renting.commonservice.exception.AppBadRequestException;
import com.chatbot_renting.commonservice.exception.AppError;
import com.chatbot_renting.commonservice.exception.AppNotFoundException;
import com.chatbot_renting.subscriptionservice.dto.request.InvoiceStatusUpdateRequest;
import com.chatbot_renting.subscriptionservice.dto.response.InvoiceStatusUpdateResponse;
import com.chatbot_renting.subscriptionservice.entity.Invoice;
import com.chatbot_renting.subscriptionservice.entity.Order;
import com.chatbot_renting.subscriptionservice.entity.Subscription;
import com.chatbot_renting.subscriptionservice.entity.SubscriptionPlan;
import com.chatbot_renting.subscriptionservice.entity.UsageSummary;
import com.chatbot_renting.subscriptionservice.entity.enums.InvoiceStatus;
import com.chatbot_renting.subscriptionservice.entity.enums.OrderStatus;
import com.chatbot_renting.subscriptionservice.entity.enums.OrderType;
import com.chatbot_renting.subscriptionservice.entity.enums.SubscriptionStatus;
import com.chatbot_renting.subscriptionservice.exception.code.SubscriptionErrorCode;
import com.chatbot_renting.subscriptionservice.repository.InvoiceRepository;
import com.chatbot_renting.subscriptionservice.repository.OrderRepository;
import com.chatbot_renting.subscriptionservice.repository.SubscriptionPlanRepository;
import com.chatbot_renting.subscriptionservice.repository.SubscriptionRepository;
import com.chatbot_renting.subscriptionservice.repository.UsageSummaryRepository;
import com.chatbot_renting.subscriptionservice.service.InvoiceService;
import com.chatbot_renting.subscriptionservice.utils.BillingPeriodUtils;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class InvoiceServiceImpl implements InvoiceService {

    private final InvoiceRepository invoiceRepository;
    private final OrderRepository orderRepository;
    private final SubscriptionRepository subscriptionRepository;
    private final SubscriptionPlanRepository planRepository;
    private final UsageSummaryRepository usageSummaryRepository;

    @Override
    @Transactional
    public InvoiceStatusUpdateResponse updateInvoiceStatus(UUID invoiceId, InvoiceStatusUpdateRequest request) {
        log.info("Starting updateInvoiceStatus - invoiceId={}, status={}", invoiceId, request.getStatus());
        try {
            Invoice invoice = invoiceRepository.findById(invoiceId)
                    .orElseThrow(() -> new AppNotFoundException(new AppError(SubscriptionErrorCode.INVOICE_NOT_FOUND)));

            if (invoice.getStatus() == InvoiceStatus.PAID || invoice.getStatus() == InvoiceStatus.CANCELLED) {
                throw new AppBadRequestException(new AppError(SubscriptionErrorCode.INVALID_STATUS_TRANSITION));
            }

            Order order = invoice.getOrder();
            Subscription subscription = order.getSubscription();

            // Apply transition and rebuild entities using builder pattern
            UpdateResult result = applyStatusTransition(invoice, order, subscription, request);

            invoiceRepository.save(result.invoice());
            orderRepository.save(result.order());
            subscriptionRepository.save(result.subscription());

            InvoiceStatusUpdateResponse resp = new InvoiceStatusUpdateResponse();
            resp.setInvoiceId(result.invoice().getId());
            resp.setInvoiceStatus(result.invoice().getStatus().name());
            resp.setOrderStatus(result.order().getStatus().name());
            resp.setSubscriptionStatus(result.subscription().getStatus().name());
            if (result.subscription().getCurrentPeriodStart() != null) {
                resp.setCurrentPeriodStart(result.subscription().getCurrentPeriodStart().toString());
            }
            if (result.subscription().getCurrentPeriodEnd() != null) {
                resp.setCurrentPeriodEnd(result.subscription().getCurrentPeriodEnd().toString());
            }
            log.info("Completed updateInvoiceStatus - invoiceId={}, subscriptionStatus={}",
                    invoiceId, result.subscription().getStatus());
            return resp;
        } catch (Exception e) {
            log.error("Error in updateInvoiceStatus - invoiceId={}, status={}", invoiceId, request.getStatus(), e);
            throw e;
        }
    }

    // ──────────────────────────── Private helpers ──────────────────────────────

    /**
     * Applies invoice status transition and returns new (rebuilt) entity instances.
     * All entity mutations use toBuilder() to avoid direct setter calls.
     */
    private UpdateResult applyStatusTransition(
            Invoice invoice,
            Order order,
            Subscription subscription,
            InvoiceStatusUpdateRequest request
    ) {
        Invoice updatedInvoice = invoice.toBuilder().status(request.getStatus()).build();
        Order updatedOrder = order;
        Subscription updatedSubscription = subscription;

        if (request.getStatus() == InvoiceStatus.PAID) {
            var paid = handlePaidInvoice(updatedInvoice, order, subscription, request);
            updatedInvoice = paid.invoice();
            updatedOrder = paid.order();
            updatedSubscription = paid.subscription();
        } else if (request.getStatus() == InvoiceStatus.FAILED) {
            var failed = handleFailedInvoice(order, subscription);
            updatedOrder = failed.order();
            updatedSubscription = failed.subscription();
        } else if (request.getStatus() == InvoiceStatus.CANCELLED) {
            var cancelled = handleCancelledInvoice(order, subscription);
            updatedOrder = cancelled.order();
            updatedSubscription = cancelled.subscription();
        }

        return new UpdateResult(updatedInvoice, updatedOrder, updatedSubscription);
    }

    private UpdateResult handlePaidInvoice(
            Invoice invoice,
            Order order,
            Subscription subscription,
            InvoiceStatusUpdateRequest request
    ) {
        Invoice updatedInvoice = invoice.toBuilder()
                .paidAt(LocalDateTime.now())
                .paymentMethod(request.getPaymentMethod())
                .paymentReference(request.getPaymentReference())
                .notes(request.getNotes())
                .build();

        Order updatedOrder = order.toBuilder().status(OrderStatus.PAID).build();
        Subscription updatedSubscription = subscription;

        if (order.getOrderType() == OrderType.NEW_SUBSCRIPTION || order.getOrderType() == OrderType.RENEWAL) {
            LocalDateTime startDate = subscription.getStartDate() != null
                    ? subscription.getStartDate()
                    : LocalDateTime.now();

            LocalDateTime periodStart = order.getOrderType() == OrderType.RENEWAL
                    && subscription.getCurrentPeriodEnd() != null
                    ? subscription.getCurrentPeriodEnd()
                    : LocalDateTime.now();

            SubscriptionPlan plan = subscription.getPlan();
            LocalDateTime periodEnd = BillingPeriodUtils.calculatePeriodEnd(
                    periodStart, subscription.getBillingCycle(), plan.getDurationMonths());

            // Apply scheduled downgrade if present
            SubscriptionPlan activePlan = plan;
            UUID scheduledPlanId = subscription.getScheduledPlanId();
            if (scheduledPlanId != null) {
                activePlan = planRepository.findById(scheduledPlanId).orElse(plan);
                scheduledPlanId = null;
            }

            updatedSubscription = subscription.toBuilder()
                    .status(SubscriptionStatus.ACTIVE)
                    .startDate(startDate)
                    .currentPeriodStart(periodStart)
                    .currentPeriodEnd(periodEnd)
                    .plan(activePlan)
                    .scheduledPlanId(scheduledPlanId)
                    .currentMaxChatbots(activePlan.getMaxChatbots())
                    .currentMaxStorageMb(activePlan.getMaxStorageMb())
                    .currentMaxMonthlyTokens(activePlan.getMaxMonthlyTokens())
                    .build();

            initUsageSummary(updatedSubscription, periodStart, periodEnd);
        } else if (order.getOrderType() == OrderType.UPGRADE) {
            SubscriptionPlan upgradedPlan = subscription.getPlan();
            updatedSubscription = subscription.toBuilder()
                    .status(SubscriptionStatus.ACTIVE)
                    .currentMaxChatbots(upgradedPlan.getMaxChatbots())
                    .currentMaxStorageMb(upgradedPlan.getMaxStorageMb())
                    .currentMaxMonthlyTokens(upgradedPlan.getMaxMonthlyTokens())
                    .build();
        }

        return new UpdateResult(updatedInvoice, updatedOrder, updatedSubscription);
    }

    private void initUsageSummary(Subscription subscription, LocalDateTime periodStart, LocalDateTime periodEnd) {
        usageSummaryRepository.findBySubscriptionIdAndPeriodStart(subscription.getId(), periodStart)
                .orElseGet(() -> usageSummaryRepository.save(UsageSummary.builder()
                        .subscription(subscription)
                        .periodStart(periodStart)
                        .periodEnd(periodEnd)
                        .tokensUsed(0L)
                        .storageUsedMb(0.0)
                        .chatbotCount(0)
                        .filesCount(0)
                        .apiCalls(0L)
                        .build()));
    }

    private record UpdateResult(Invoice invoice, Order order, Subscription subscription) {}

    private record OrderSubResult(Order order, Subscription subscription) {}

    private OrderSubResult handleFailedInvoice(Order order, Subscription subscription) {
        Order updatedOrder = order.toBuilder().status(OrderStatus.FAILED).build();
        Subscription updatedSubscription = subscription;

        if (order.getOrderType() == OrderType.RENEWAL) {
            updatedSubscription = subscription.toBuilder().status(SubscriptionStatus.PAST_DUE).build();
        } else if (order.getOrderType() == OrderType.UPGRADE) {
            updatedSubscription = subscription.toBuilder()
                    .plan(subscription.getPreviousPlan())
                    .build();
        }
        return new OrderSubResult(updatedOrder, updatedSubscription);
    }

    private OrderSubResult handleCancelledInvoice(Order order, Subscription subscription) {
        Order updatedOrder = order.toBuilder().status(OrderStatus.CANCELLED).build();
        Subscription updatedSubscription = subscription;

        if (order.getOrderType() == OrderType.NEW_SUBSCRIPTION) {
            updatedSubscription = subscription.toBuilder().status(SubscriptionStatus.EXPIRED).build();
        }
        return new OrderSubResult(updatedOrder, updatedSubscription);
    }
}
