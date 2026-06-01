package com.chatbot_renting.subscriptionservice.controller;

import com.chatbot_renting.subscriptionservice.dto.request.Order.CreateOrderRequest;
import com.chatbot_renting.subscriptionservice.dto.response.OrderResponse;
import com.chatbot_renting.subscriptionservice.service.OrderService;
import com.lecturemind.commonservice.domain.Response.ApiResponse;
import com.lecturemind.commonservice.domain.Response.ResponseBuilder;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    public ResponseEntity<ApiResponse<OrderResponse>> createOrder(
            @RequestHeader("X-User-Id") Long userId,
            @RequestBody CreateOrderRequest request
    ) {
        request.setUserId(userId);

        return ResponseEntity.ok(
                ResponseBuilder.created(orderService.createOrder(request))
        );
    }

    @PostMapping("/{orderId}/pay")
    public ResponseEntity<ApiResponse<OrderResponse>> payOrder(
            @RequestHeader("X-User-Id") Long userId,
            @PathVariable Long orderId
    ) {
        return ResponseEntity.ok(
                ResponseBuilder.success(orderService.payOrder(orderId, userId))
        );
    }

    @PutMapping("/{orderId}/cancel")
    public ResponseEntity<ApiResponse<OrderResponse>> cancelOrder(
            @RequestHeader("X-User-Id") Long userId,
            @PathVariable Long orderId,
            @RequestParam(required = false) String reason
    ) {
        return ResponseEntity.ok(
                ResponseBuilder.success(orderService.cancelOrder(orderId, reason))
        );
    }


    @PutMapping("/{orderId}/fail")
    public ResponseEntity<ApiResponse<OrderResponse>> failOrder(
            @RequestHeader("X-User-Id") Long userId,
            @PathVariable Long orderId,
            @RequestParam(required = false) String reason
    ) {
        return ResponseEntity.ok(
                ResponseBuilder.success(orderService.failOrder(orderId, reason))
        );
    }

    @PostMapping("/{orderId}/refund")
    public ResponseEntity<ApiResponse<OrderResponse>> refundOrder(
            @RequestHeader("X-User-Id") Long userId,
            @PathVariable Long orderId,
            @RequestParam(required = false) String reason
    ) {
        return ResponseEntity.ok(
                ResponseBuilder.success(orderService.refundOrder(orderId, reason))
        );
    }


    @GetMapping("/{orderId}")
    public ResponseEntity<ApiResponse<OrderResponse>> getOrder(
            @RequestHeader("X-User-Id") Long userId,
            @PathVariable Long orderId
    ) {
        return ResponseEntity.ok(
                ResponseBuilder.success(orderService.getOrder(orderId))
        );
    }

    @GetMapping("/user")
    public ResponseEntity<ApiResponse<List<OrderResponse>>> getOrdersByUser(
            @RequestHeader("X-User-Id") Long userId
    ) {
        return ResponseEntity.ok(
                ResponseBuilder.success(orderService.getOrdersByUser(userId))
        );
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<OrderResponse>>> getAllOrders(
            @RequestHeader("X-User-Id") Long userId
    ) {
        return ResponseEntity.ok(
                ResponseBuilder.success(orderService.getAllOrder(null))
        );
    }
}