package com.chatbot_renting.notificationservice.dispatch;

import java.util.UUID;

// Impl thật (KafkaDeliveryDispatchPublisher) tự viết theo Kafka bạn đã chọn,
// publish message dạng { "deliveryId": "..." } vào topic notification.<channel>.dispatch
public interface DeliveryDispatchPublisher {
    void publish(UUID deliveryId, String channel);
}