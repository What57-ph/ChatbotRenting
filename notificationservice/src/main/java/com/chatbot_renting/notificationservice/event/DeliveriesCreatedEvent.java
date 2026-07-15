package main.java.com.chatbot_renting.notificationservice.event;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

import java.util.List;
import java.util.UUID;

@Getter
public class DeliveriesCreatedEvent extends ApplicationEvent {
    // deliveryId -> channel, để publisher biết đẩy vào topic nào
    private final List<DeliveryRef> deliveries;

    public DeliveriesCreatedEvent(Object source, List<DeliveryRef> deliveries) {
        super(source);
        this.deliveries = deliveries;
    }

    public record DeliveryRef(UUID deliveryId, String channel) {
    }
}