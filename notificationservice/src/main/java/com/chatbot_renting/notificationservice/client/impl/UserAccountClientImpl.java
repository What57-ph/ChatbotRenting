package com.chatbot_renting.notificationservice.client.impl;

import com.chatbot_renting.notificationservice.client.UserAccountClient;
import com.chatbot_renting.notificationservice.config.client.AbstractRestClientConfiguration;
import com.chatbot_renting.notificationservice.config.properties.UserAccountProperties;
import com.chatbot_renting.notificationservice.dto.response.client.AllUsersResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.restclient.RestTemplateBuilder;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.List;

@Service
@Slf4j
public class UserAccountClientImpl extends AbstractRestClientConfiguration implements UserAccountClient {

    public static final String SERVICE_ID = "authservice";
    private final UserAccountProperties properties;
    private RestTemplate restTemplate = getRestTemplate();
    public UserAccountClientImpl(String serviceId, RestTemplateBuilder restTemplateBuilder, UserAccountProperties properties) {
        super(SERVICE_ID, restTemplateBuilder);
        this.properties = properties;
    }

    @Override
    public AllUsersResponse getAllUsers() {
        log.info("Start getAllUsers to auth customer");

        URI uri = UriComponentsBuilder.fromUriString(properties.getEndpoints().getGetAllUsers())
                .build().toUri();

        HttpHeaders headers = new HttpHeaders();
        HttpEntity<?> entity = new HttpEntity<>(headers);

        AllUsersResponse response = restTemplate.exchange(
                buildBasePath() + uri,
                HttpMethod.GET,
                entity,
                AllUsersResponse.class
        ).getBody();

        log.info("End getAllUsers to auth customer with response: [{}]", response);

        return response;
    }
}
