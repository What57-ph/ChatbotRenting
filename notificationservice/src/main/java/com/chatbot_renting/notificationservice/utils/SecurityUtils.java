package com.chatbot_renting.notificationservice.utils;

import org.springframework.stereotype.Component;
import java.util.UUID;

@Component
public class SecurityUtils {

    /**
     * For now, this returns a mock ID.
     * In a real environment, this should parse the JWT token from the Request Context.
     */
    public UUID getCurrentUserId() {
        return UUID.randomUUID();
    }
}
