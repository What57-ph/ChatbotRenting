package com.chatbot_renting.subscriptionservice.utils;

import com.chatbot_renting.subscriptionservice.entity.enums.BillingCycle;

import java.time.LocalDateTime;

public final class BillingPeriodUtils {

    private BillingPeriodUtils() {
    }

    /**
     * Tính periodEnd từ billing anchor (currentPeriodStart) và chu kỳ thanh toán hiện tại.
     */
    public static LocalDateTime calculatePeriodEnd(
            LocalDateTime periodStart,
            BillingCycle billingCycle,
            Integer durationMonths
    ) {
        if (durationMonths != null && durationMonths > 0) {
            return periodStart.plusMonths(durationMonths);
        }
        return billingCycle == BillingCycle.YEARLY
                ? periodStart.plusYears(1)
                : periodStart.plusMonths(1);
    }
}
