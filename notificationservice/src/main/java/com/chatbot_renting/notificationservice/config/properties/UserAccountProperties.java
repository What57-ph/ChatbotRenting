package com.chatbot_renting.notificationservice.config.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "application.custom.authservice")
public class UserAccountProperties {
    private Endpoints endpoints;

    @Data
    public static class Endpoints {
        private String getAllUsers;
    }
}
