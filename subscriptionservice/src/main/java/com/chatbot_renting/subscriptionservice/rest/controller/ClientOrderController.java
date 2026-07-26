package com.chatbot_renting.subscriptionservice.rest.controller;

import com.chatbot_renting.subscriptionservice.dto.response.OrderDto;
import com.chatbot_renting.subscriptionservice.dto.response.PagedResponse;
import com.chatbot_renting.subscriptionservice.rest.api.ClientOrderApi;
import com.chatbot_renting.subscriptionservice.service.OrderService;
import com.chatbot_renting.subscriptionservice.utils.SecurityUtils;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Slf4j
public class ClientOrderController implements ClientOrderApi {

    private final OrderService orderService;
    private final SecurityUtils securityUtils;

    @Override
    public ResponseEntity<PagedResponse<OrderDto>> getOrders(int page, int limit, String status) {
        UUID userId = securityUtils.getUserIdOrElseThrow();
        log.info("Fetching orders for user {} page {}", userId, page);
        return ResponseEntity.ok(orderService.getUserOrders(userId, page, limit, status));
    }

    @Override
    public ResponseEntity<OrderDto> getOrder(UUID orderId) {
        UUID userId = securityUtils.getUserIdOrElseThrow();
        log.info("Fetching order {} for user {}", orderId, userId);
        return ResponseEntity.ok(orderService.getOrder(userId, orderId));
    }
}
