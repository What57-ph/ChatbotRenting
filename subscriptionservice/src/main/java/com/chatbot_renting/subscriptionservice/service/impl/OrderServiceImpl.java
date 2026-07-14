package com.chatbot_renting.subscriptionservice.service.impl;

import com.chatbot_renting.commonservice.exception.AppError;
import com.chatbot_renting.commonservice.exception.AppNotFoundException;
import com.chatbot_renting.subscriptionservice.dto.response.OrderDto;
import com.chatbot_renting.subscriptionservice.dto.response.PagedResponse;
import com.chatbot_renting.subscriptionservice.entity.Order;
import com.chatbot_renting.subscriptionservice.entity.enums.OrderStatus;
import com.chatbot_renting.subscriptionservice.exception.code.SubscriptionErrorCode;
import com.chatbot_renting.subscriptionservice.mapper.OrderMapper;
import com.chatbot_renting.subscriptionservice.repository.OrderRepository;
import com.chatbot_renting.subscriptionservice.service.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final OrderMapper orderMapper;

    @Override
    public PagedResponse<OrderDto> getUserOrders(Long userId, int page, int limit, String status) {
        log.info("Starting getUserOrders - userId={}, page={}, limit={}, status={}", userId, page, limit, status);
        try {
            Page<Order> orderPage;
            if (status != null && !status.isEmpty()) {
                OrderStatus orderStatus = OrderStatus.valueOf(status.toUpperCase());
                orderPage = orderRepository.findByUserIdAndStatusOrderByCreatedAtDesc(
                        userId, orderStatus, PageRequest.of(page - 1, limit));
            } else {
                orderPage = orderRepository.findByUserIdOrderByCreatedAtDesc(
                        userId, PageRequest.of(page - 1, limit));
            }

            List<OrderDto> dtoList = orderPage.getContent().stream()
                    .map(orderMapper::toDto)
                    .collect(Collectors.toList());

            PagedResponse<OrderDto> response = new PagedResponse<>(
                    dtoList, new PagedResponse.Pagination(page, limit, orderPage.getTotalElements()));
            log.info("Completed getUserOrders - userId={}, total={}", userId, orderPage.getTotalElements());
            return response;
        } catch (Exception e) {
            log.error("Error in getUserOrders - userId={}", userId, e);
            throw e;
        }
    }

    @Override
    public OrderDto getOrder(Long userId, Long orderId) {
        log.info("Starting getOrder - userId={}, orderId={}", userId, orderId);
        try {
            Order order = orderRepository.findById(orderId)
                    .filter(o -> o.getUserId().equals(userId))
                    .orElseThrow(() -> new AppNotFoundException(new AppError(SubscriptionErrorCode.ORDER_NOT_FOUND)));
            OrderDto dto = orderMapper.toDto(order);
            log.info("Completed getOrder - userId={}, orderId={}", userId, orderId);
            return dto;
        } catch (Exception e) {
            log.error("Error in getOrder - userId={}, orderId={}", userId, orderId, e);
            throw e;
        }
    }
}
