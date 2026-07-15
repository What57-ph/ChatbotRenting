INSERT INTO notification_templates (id, code, name, description, created_at, updated_at) VALUES 
(gen_random_uuid(), 'SUBSCRIPTION_EXPIRING', 'Subscription Expiring Soon', 'Sent when a user''s subscription is about to expire', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(gen_random_uuid(), 'TOKEN_LIMIT_WARNING', 'Token Limit Warning', 'Sent when the user is nearing their AI token limit', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(gen_random_uuid(), 'PAYMENT_FAILED', 'Payment Failed', 'Sent when a subscription renewal or plan purchase fails', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(gen_random_uuid(), 'SUBSCRIPTION_SUCCESS', 'Subscription Activated', 'Sent when a user successfully subscribes to a plan', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(gen_random_uuid(), 'SYSTEM_ANNOUNCEMENT', 'Global System Announcement', 'Important system-wide messages or maintenance alerts', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(gen_random_uuid(), 'CHATBOT_FALLBACK', 'Chatbot Error Encountered', 'Sent when the chatbot encounters an unhandled situation or query', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)
ON CONFLICT (code) DO NOTHING;
