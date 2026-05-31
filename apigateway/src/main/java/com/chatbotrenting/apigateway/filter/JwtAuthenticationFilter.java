package com.chatbotrenting.apigateway.filter;

import com.chatbotrenting.apigateway.client.AuthClient;
import com.chatbotrenting.apigateway.model.Token;
import com.lecturemind.commonservice.domain.Response.ApiResponse;
import feign.FeignException;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.List;

@Component("JwtAuthenticationFilter")
@Slf4j
public class JwtAuthenticationFilter extends AbstractGatewayFilterFactory<JwtAuthenticationFilter.Config> {

    public JwtAuthenticationFilter() {
        super(Config.class);
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            String path = exchange.getRequest().getURI().getPath();

            // Public endpoints - skip auth
            if (config != null && config.getPublicEndpoints().stream().anyMatch(path::startsWith)) {
                return chain.filter(exchange);
            }

            String authorizationHeader = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);

            log.debug("Path: {}, Auth header present: {}", path, authorizationHeader != null);

            if (!Token.isBearerToken(authorizationHeader)) {
                log.warn("Missing or invalid Bearer token for path: {}", path);
                exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                return exchange.getResponse().setComplete();
            }

            String jwt = Token.getJwt(authorizationHeader);
            ApplicationContext context = exchange.getApplicationContext();
            AuthClient authClient = context.getBean(AuthClient.class);

            return Mono.fromCallable(() -> {
                        ResponseEntity<Long> response = authClient.validateToken(jwt);
                        if (response == null){
                            throw new FeignException.Unauthorized(
                                    "Missing userId from auth-service",
                                    null, null, null
                            );
                        }
                        Long userId = response.getBody();

                        log.debug("Token validated, userId: {}", userId);
                        return userId;
                    })
                    .subscribeOn(Schedulers.boundedElastic())
                    .flatMap(userId -> {
                        // Inject X-User-Id so downstream services can identify the caller
                        var mutatedRequest = exchange.getRequest().mutate()
                                .header("X-User-Id", String.valueOf(userId))
                                .build();
                        return chain.filter(exchange.mutate().request(mutatedRequest).build());
                    })
                    .onErrorResume(e -> {
                        log.error("Token validation failed for path: {}, error: {}", path, e.getMessage());
                        if (e instanceof FeignException.Unauthorized
                                || e instanceof FeignException.Forbidden
                                || e instanceof FeignException fex && fex.status() == 401) {
                            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                        } else {
                            exchange.getResponse().setStatusCode(HttpStatus.SERVICE_UNAVAILABLE);
                        }
                        return exchange.getResponse().setComplete();
                    });
        };
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Config{
        private List<String> publicEndpoints;
    }
}
