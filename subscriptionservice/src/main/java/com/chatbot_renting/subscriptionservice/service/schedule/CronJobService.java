package com.chatbot_renting.subscriptionservice.service.schedule;

import com.chatbot_renting.subscriptionservice.entity.Invoice;
import com.chatbot_renting.subscriptionservice.entity.Order;
import com.chatbot_renting.subscriptionservice.entity.Subscription;
import com.chatbot_renting.subscriptionservice.entity.SubscriptionPlan;
import com.chatbot_renting.subscriptionservice.entity.enums.InvoiceStatus;
import com.chatbot_renting.subscriptionservice.entity.enums.OrderStatus;
import com.chatbot_renting.subscriptionservice.entity.enums.OrderType;
import com.chatbot_renting.subscriptionservice.entity.enums.SubscriptionStatus;
import com.chatbot_renting.subscriptionservice.repository.InvoiceRepository;
import com.chatbot_renting.subscriptionservice.repository.OrderRepository;
import com.chatbot_renting.subscriptionservice.repository.SubscriptionRepository;
import com.chatbot_renting.subscriptionservice.repository.SubscriptionPlanRepository;
import com.chatbot_renting.subscriptionservice.utils.PlanUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@EnableScheduling
@Slf4j
public class CronJobService {

    private static final DateTimeFormatter ORDER_DATE_FMT = DateTimeFormatter.ofPattern("yyyyMMdd");

    private final SubscriptionRepository subscriptionRepository;
    private final OrderRepository orderRepository;
    private final InvoiceRepository invoiceRepository;
    private final SubscriptionPlanRepository planRepository;
    private final PlanUtils planUtils;

    @Scheduled(cron = "0 0 1 * * ?")
    @Transactional
    public void autoRenewSubscriptions() {
        log.info("Starting autoRenewSubscriptions job");
        try {
            LocalDateTime targetDate = LocalDateTime.now().plusDays(1);
            List<Subscription> subscriptions = subscriptionRepository.findByStatusAndAutoRenewTrueAndCurrentPeriodEndLessThanEqual(SubscriptionStatus.ACTIVE, targetDate);

            for (Subscription sub : subscriptions) {
                try {
                    processRenewal(sub);
                } catch (Exception e) {
                    log.error("Error auto-renewing subscription {}: {}", sub.getId(), e.getMessage(), e);
                }
            }
            log.info("Completed autoRenewSubscriptions job - processed={}", subscriptions.size());
        } catch (Exception e) {
            log.error("Error in autoRenewSubscriptions job", e);
            throw e;
        }
    }

    private void processRenewal(Subscription sub) {
        if (!sub.getPlan().getActive()) {
            log.error("Subscription {} plan inactive", sub.getId());
            return;
        }

        if (orderRepository.existsBySubscriptionIdAndOrderTypeAndStatus(
                sub.getId(), OrderType.RENEWAL, OrderStatus.PENDING)) {
            return;
        }

        SubscriptionPlan activePlan = sub.getPlan();
        if (sub.getScheduledPlanId() != null) {
            activePlan = planRepository.findById(sub.getScheduledPlanId()).orElse(sub.getPlan());
        }

        double amount = planUtils.resolvePrice(activePlan, sub.getBillingCycle());
        String planSnapshot = planUtils.buildPlanSnapshot(activePlan, sub.getBillingCycle(), amount);

        Order order = Order.builder()
                .subscription(sub)
                .userId(sub.getUserId())
                .orderType(OrderType.RENEWAL)
                .status(OrderStatus.PENDING)
                .billingCycle(sub.getBillingCycle())
                .amount(amount)
                .orderNumber(generateNumber("ORD-RN"))
                .planSnapshot(planSnapshot)
                .build();
        orderRepository.save(order);

        Invoice invoice = Invoice.builder()
                .order(order)
                .amount(amount)
                .status(InvoiceStatus.UNPAID)
                .invoiceNumber(generateNumber("INV-RN"))
                .issuedAt(LocalDateTime.now())
                .dueDate(LocalDateTime.now().plusDays(1))
                .planSnapshot(planSnapshot)
                .build();
        invoiceRepository.save(invoice);

        log.info("Created renewal order for subscription {} with billingCycle={}",
                sub.getId(), sub.getBillingCycle());
    }

    @Scheduled(cron = "0 0 2 * * ?")
    @Transactional
    public void expireSubscriptions() {
        log.info("Starting expireSubscriptions job");
        try {
            LocalDateTime now = LocalDateTime.now();
            List<Subscription> subscriptionsToArchive = subscriptionRepository.findByStatusInAndAutoRenewFalseAndCurrentPeriodEndLessThan(
                    java.util.Arrays.asList(SubscriptionStatus.ACTIVE, SubscriptionStatus.TRIALING), now);
            for (Subscription sub : subscriptionsToArchive) {
                Subscription expired = Subscription.builder()
                        .status(SubscriptionStatus.EXPIRED).build();
                subscriptionRepository.save(expired);
                log.info("Subscription {} expired", sub.getId());
            }

            List<Subscription> pastDueSubscriptions = subscriptionRepository.findByStatusAndCurrentPeriodEndLessThan(
                    SubscriptionStatus.PAST_DUE, now.minusDays(7));
            for (Subscription sub : pastDueSubscriptions) {
                Subscription expired = Subscription.builder().status(SubscriptionStatus.EXPIRED).build();
                subscriptionRepository.save(expired);
                log.info("Subscription {} expired from past due", sub.getId());
            }
            log.info("Completed expireSubscriptions job - waiting={}, pastDue={}",
                    subscriptionsToArchive.size(), pastDueSubscriptions.size());
        } catch (Exception e) {
            log.error("Error in expireSubscriptions job", e);
            throw e;
        }
    }

    @Scheduled(cron = "0 0 8 * * ?")
    @Transactional
    public void executePreTrialBilling() {
        log.info("Starting executePreTrialBilling job");
        try {
            LocalDateTime targetDate = LocalDateTime.now().plusDays(3);
            LocalDateTime startOfDay = targetDate.with(java.time.LocalTime.MIN);
            LocalDateTime endOfDay = targetDate.with(java.time.LocalTime.MAX);

            List<Subscription> expiringTrials = subscriptionRepository.findByStatusAndAutoRenewTrueAndCurrentPeriodEndGreaterThanEqualAndCurrentPeriodEndLessThan(
                    SubscriptionStatus.TRIALING, startOfDay, endOfDay);

            for (Subscription sub : expiringTrials) {
                if (orderRepository.existsBySubscriptionIdAndOrderTypeAndStatus(
                        sub.getId(), OrderType.NEW_SUBSCRIPTION, OrderStatus.PENDING)) {
                    continue;
                }

                double amount = planUtils.resolvePrice(sub.getPlan(), sub.getBillingCycle());
                String planSnapshot = planUtils.buildPlanSnapshot(sub.getPlan(), sub.getBillingCycle(), amount);

                Order order = Order.builder()
                        .subscription(sub)
                        .userId(sub.getUserId())
                        .orderType(OrderType.NEW_SUBSCRIPTION)
                        .status(OrderStatus.PENDING)
                        .billingCycle(sub.getBillingCycle())
                        .amount(amount)
                        .orderNumber(generateNumber("ORD-TR"))
                        .planSnapshot(planSnapshot)
                        .build();
                orderRepository.save(order);

                Invoice invoice = Invoice.builder()
                        .order(order)
                        .amount(amount)
                        .status(InvoiceStatus.UNPAID)
                        .invoiceNumber(generateNumber("INV-TR"))
                        .issuedAt(LocalDateTime.now())
                        .dueDate(LocalDateTime.now().plusDays(3))
                        .planSnapshot(planSnapshot)
                        .build();
                invoiceRepository.save(invoice);

                log.info("Created pre-trial billing order for subscription {} with amount={}", sub.getId(), amount);
            }
            log.info("Completed executePreTrialBilling job - processed={}", expiringTrials.size());
        } catch (Exception e) {
            log.error("Error in executePreTrialBilling job", e);
            throw e;
        }
    }

    private String generateNumber(String prefix) {
        return prefix + "-" + LocalDateTime.now().format(ORDER_DATE_FMT) + "-"
                + UUID.randomUUID().toString().substring(0, 6).toUpperCase();
    }
}
