package com.chatbot_renting.subscriptionservice.utils;

/**
 * Các featureKey chuẩn lưu trong bảng plan_features.
 * Giá theo chu kỳ và cấu hình dynamic đều nằm ở PlanFeature, không trên SubscriptionPlan.
 */
public final class PlanFeatureKeys {

    public static final String MONTHLY_PRICE = "monthly_price";
    public static final String YEARLY_PRICE = "yearly_price";

    private PlanFeatureKeys() {
    }
}
