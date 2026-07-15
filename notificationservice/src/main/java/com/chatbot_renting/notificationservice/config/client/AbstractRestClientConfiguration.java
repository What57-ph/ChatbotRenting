package com.chatbot_renting.notificationservice.config.client;


import jakarta.annotation.PostConstruct;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.restclient.RestTemplateBuilder;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;

@Data
@Slf4j
public class AbstractRestClientConfiguration {
    @Pattern(regexp = "^(http|https)$")
    private String schema = "http";

    private int connectTimeoutMs = 3000;

    private int readTimeoutMs = 5000;

    private final Retry retry = new Retry();

    private final String serviceId;

    private RestTemplate restTemplate;

    protected AbstractRestClientConfiguration(String serviceId, RestTemplateBuilder restTemplateBuilder){
        this.serviceId = serviceId;
        this.restTemplate = restTemplateBuilder
                .connectTimeout(Duration.ofMillis(this.connectTimeoutMs))
                .readTimeout(Duration.ofMillis(this.readTimeoutMs))
                .build();
    }

    protected String buildBasePath(){
        return String.format("%s://%s", this.schema, this.serviceId);
    }

    @Data
    public static class Retry {
        private int maxAttempts = 1;

        private long backoffMs = 200;
    }
}
