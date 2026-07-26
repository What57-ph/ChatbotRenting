package com.chatbot_renting.subscriptionservice.rest.api;

import com.chatbot_renting.subscriptionservice.dto.response.OrderDto;
import com.chatbot_renting.subscriptionservice.dto.response.PagedResponse;
import java.util.UUID;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Validated
@RequestMapping("/client-api/v1/orders")
public interface ClientOrderApi {

    @GetMapping
    ResponseEntity<PagedResponse<OrderDto>> getOrders(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int limit,
            @RequestParam(required = false) String status);

    @GetMapping("/{orderId}")
    ResponseEntity<OrderDto> getOrder(@PathVariable("orderId") UUID orderId);
}
