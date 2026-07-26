package com.chatbot_renting.subscriptionservice.exception.code;

import com.chatbot_renting.commonservice.exception.code.AppErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum SubscriptionErrorCode implements AppErrorCode {

    INVALID_BILLING_CYCLE("SUB-001", "Invalid billing cycle (must be MONTHLY or YEARLY)"),
    SAME_PLAN("SUB-002", "New plan is the same as the current plan"),
    USE_DOWNGRADE_ENDPOINT("SUB-003", "Please use the downgrade endpoint for lower priced plans"),
    USE_UPGRADE_ENDPOINT("SUB-004", "Please use the upgrade endpoint for higher priced plans"),
    CHATBOT_LIMIT_EXCEEDED("SUB-005", "Current chatbot usage exceeds the new plan's limit"),
    STORAGE_LIMIT_EXCEEDED("SUB-006", "Current storage usage exceeds the new plan's limit"),
    SUBSCRIPTION_PENDING("SUB-007", "Subscription is pending, cannot cancel"),
    SUBSCRIPTION_NOT_ACTIVE("SUB-008", "Action requires an active subscription"),
    PLAN_NOT_FOUND("SUB-009", "Subscription plan not found or inactive"),
    SUBSCRIPTION_NOT_FOUND("SUB-010", "Subscription not found"),
    ORDER_NOT_FOUND("SUB-011", "Order not found"),
    INVOICE_NOT_FOUND("SUB-012", "Invoice not found"),
    SUBSCRIPTION_ALREADY_EXISTS("SUB-013", "User already has an active or pending subscription"),
    ALREADY_WAITING_TO_EXPIRED("SUB-014", "Subscription is already cancelled and waiting to expire"),
    INVALID_STATUS_TRANSITION("SUB-015", "Invalid invoice status transition"),
    NO_ACTIVE_SUBSCRIPTION("SUB-016", "No active subscription for user"),
    TOKEN_LIMIT_EXCEEDED("SUB-017", "Token limit exceeded for current plan"),
    TRIAL_CANNOT_BE_CHANGED("SUB-018", "Subscription plan cannot be changed during trial period"),
    PLAN_CODE_ALREADY_EXISTS("SUB-019", "Subscription plan code already exists"),
    PLAN_MISSING_PRICE("SUB-020", "Subscription plan must include monthly_price and yearly_price features"),
    UNAUTHORIZED("SUB-401", "Missing or invalid JWT token — authentication required");

    private final String code;
    private final String message;
}
