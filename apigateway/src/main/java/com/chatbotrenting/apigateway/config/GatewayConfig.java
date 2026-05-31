package com.chatbotrenting.apigateway.config;

import com.chatbotrenting.apigateway.filter.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import java.util.List;

@Configuration
@RequiredArgsConstructor
public class GatewayConfig {
    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    private static final List<String> PUBLIC_ENDPOINTS = List.of(
            "/api/v1/auth/register",
            "/api/v1/auth/login",
            "/api/v1/auth/refresh-token",
            "/api/v1/auth/logout",
            "/api/v1/auth/validate-token"
    );

    @Bean
    public RouteLocator routes(RouteLocatorBuilder builder) {
        JwtAuthenticationFilter.Config authConfig = new JwtAuthenticationFilter.Config(PUBLIC_ENDPOINTS);
        return builder.routes()
                .route("authservice", r -> r.path("/api/v1/auth/**")
                        .filters(f -> f.filter(jwtAuthenticationFilter.apply(authConfig)))
                        .uri("http://localhost:9000"))
                .route("ai-service", r -> r.path("/api/v1/ai/**")
                        .filters(f -> f.filter(jwtAuthenticationFilter.apply(authConfig)))
                        .uri("http://localhost:8000"))
                .route("core-service", r -> r.path("/api/v1/chatbots/**")
                        .filters(f -> f.filter(jwtAuthenticationFilter.apply(authConfig)))
                        .uri("http://localhost:9001"))
                .route("file-service", r -> r.path("/api/v1/files/**")
                        .filters(f -> f.filter(jwtAuthenticationFilter.apply(authConfig)))
                        .uri("http://localhost:9002"))
                .build();
    }
}
