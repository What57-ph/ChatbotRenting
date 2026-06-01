package com.chatbot_renting.subscriptionservice.service.impl;

import com.chatbot_renting.subscriptionservice.dto.request.Order.CreateOrderRequest;
import com.chatbot_renting.subscriptionservice.dto.request.Order.UpdateOrderStatusRequest;
import com.chatbot_renting.subscriptionservice.dto.response.OrderResponse;
import com.chatbot_renting.subscriptionservice.entity.*;
import com.chatbot_renting.subscriptionservice.entity.enums.OrderStatus;
import com.chatbot_renting.subscriptionservice.entity.enums.SubscriptionStatus;
import com.chatbot_renting.subscriptionservice.mapper.OrderMapper;
import com.chatbot_renting.subscriptionservice.repository.*;
import com.chatbot_renting.subscriptionservice.service.InvoiceService;
import com.chatbot_renting.subscriptionservice.service.OrderService;
import com.chatbot_renting.subscriptionservice.service.SubscriptionService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final SubscriptionRepository subscriptionRepository;
    private final SubscriptionPlanRepository planRepository;
    private final InvoiceRepository invoiceRepository;
    private final OrderMapper orderMapper;
    private final SubscriptionService subscriptionService;
    private final InvoiceService invoiceService;

    @Override
    @Transactional
    public OrderResponse createOrder(CreateOrderRequest request) {

        SubscriptionPlan plan = planRepository.findById(request.getPlanId())
                .orElseThrow(() -> new RuntimeException("Plan not found"));

        Order order = new Order();
        order.setUserId(request.getUserId());
        order.setPlan(plan);
        order.setBillingCycle(request.getBillingCycle());

        order.setStatus(OrderStatus.PENDING);
        order.setOrderNumber(generateOrderNumber());

        order.setAmount(request.getAmount());

        return orderMapper.toResponse(orderRepository.save(order));
    }

    @Override
    @Transactional
    public OrderResponse payOrder(Long orderId, Long userId) {

        Order order = getOrderOrThrow(orderId);

        validateTransition(order.getStatus(), OrderStatus.PAID);

        order.setStatus(OrderStatus.PAID);
        order.setCurrency("VND");

        Subscription subscription = subscriptionService.createSubscription(order, userId);
        Invoice invoice = invoiceService.createInvoice(order);

        order.setSubscription(subscription);
        orderRepository.save(order);

        OrderResponse response = orderMapper.toResponse(order);
        response.setInvoiceId(invoice.getId());
        response.setSubscriptionId(subscription.getId());
        return response;
    }

    @Override
    @Transactional
    public OrderResponse cancelOrder(Long orderId, String reason) {

        Order order = getOrderOrThrow(orderId);

        validateTransition(order.getStatus(), OrderStatus.CANCELLED);

        order.setStatus(OrderStatus.CANCELLED);

        return orderMapper.toResponse(orderRepository.save(order));
    }

    @Override
    @Transactional
    public OrderResponse failOrder(Long orderId, String reason) {

        Order order = getOrderOrThrow(orderId);

        validateTransition(order.getStatus(), OrderStatus.FAILED);

        order.setStatus(OrderStatus.FAILED);

        return orderMapper.toResponse(orderRepository.save(order));
    }

    @Override
    @Transactional
    public OrderResponse refundOrder(Long orderId, String reason) {

        Order order = getOrderOrThrow(orderId);

        if (order.getStatus() != OrderStatus.PAID) {
            throw new IllegalStateException("Only PAID order can be refunded");
        }

        order.setStatus(OrderStatus.REFUNDED);

        invoiceService.markRefund(order.getInvoice());

        subscriptionService.revokeSubscription(order.getSubscription());

        return orderMapper.toResponse(orderRepository.save(order));
    }


    @Override
    public OrderResponse getOrder(Long orderId) {

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        return orderMapper.toResponse(order);
    }

    @Override
    public List<OrderResponse> getAllOrder(Pageable pageable) {
        return orderRepository.findAll(pageable)
                .stream()
                .map(orderMapper::toResponse)
                .toList();
    }

    @Override
    public List<OrderResponse> getOrdersByUser(Long userId) {

        return orderRepository.findByUserId(userId)
                .stream()
                .map(orderMapper::toResponse)
                .toList();
    }

    private void validateTransition(OrderStatus current, OrderStatus next) {

        if (current == OrderStatus.CANCELLED) {
            throw new IllegalStateException("Order already canceled");
        }

        if (current == OrderStatus.PAID && next == OrderStatus.PENDING) {
            throw new IllegalStateException("Cannot revert PAID to PENDING");
        }
    }

    private String generateOrderNumber() {
        return "ORD-" + System.currentTimeMillis();
    }

    private Order getOrderOrThrow(Long orderId) {
        return orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));
    }

}
