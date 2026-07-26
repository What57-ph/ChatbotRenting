package com.chatbot_renting.subscriptionservice.service.impl;

import com.chatbot_renting.commonservice.exception.AppNotFoundException;
import com.chatbot_renting.subscriptionservice.dto.response.OrderDto;
import com.chatbot_renting.subscriptionservice.dto.response.PagedResponse;
import com.chatbot_renting.subscriptionservice.entity.Order;
import com.chatbot_renting.subscriptionservice.entity.enums.OrderStatus;
import com.chatbot_renting.subscriptionservice.mapper.OrderMapper;
import com.chatbot_renting.subscriptionservice.repository.OrderRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.Collections;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OrderServiceImplTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private OrderMapper orderMapper;

    @InjectMocks
    private OrderServiceImpl orderService;

    @Test
    void getUserOrders_ReturnsPagedResponse() {
        // Arrange
        UUID userId = UUID.randomUUID();
        UUID orderId = UUID.randomUUID();

        Order order = Order.builder().id(orderId).build();
        Page<Order> orderPage = new PageImpl<>(Collections.singletonList(order));

        when(orderRepository.findByUserIdAndStatusOrderByCreatedAtDesc(eq(userId), eq(OrderStatus.PAID), any(PageRequest.class)))
                .thenReturn(orderPage);

        OrderDto orderDto = new OrderDto();
        orderDto.setId(orderId);
        when(orderMapper.toDto(any(Order.class))).thenReturn(orderDto);

        // Act
        PagedResponse<OrderDto> result = orderService.getUserOrders(userId, 1, 10, "PAID");

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getData().size());
        assertEquals(orderId, result.getData().get(0).getId()); // Sửa: So sánh UUID thay vì 1L
    }

    @Test
    void getOrder_Exists_ReturnsDto() {
        // Arrange
        UUID userId = UUID.randomUUID();
        UUID orderId = UUID.randomUUID();

        Order order = Order.builder().id(orderId).userId(userId).build();
        // Giả sử repository tìm kiếm theo orderId
        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));

        OrderDto orderDto = new OrderDto();
        orderDto.setId(orderId);
        when(orderMapper.toDto(order)).thenReturn(orderDto);

        // Act
        // Lưu ý: Đảm bảo thứ tự tham số truyền vào UserService khớp với phương thức thật
        // (Trong file này, tôi giả định đang gọi theo dạng userId, orderId)
        OrderDto result = orderService.getOrder(userId, orderId);

        // Assert
        assertNotNull(result);
        assertEquals(orderId, result.getId()); // Sửa: So sánh UUID thay vì 2L
    }

    @Test
    void getOrder_NotFound_ThrowsException() {
        // Arrange
        UUID userId = UUID.randomUUID();
        UUID orderId = UUID.randomUUID();

        when(orderRepository.findById(orderId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(AppNotFoundException.class, () -> orderService.getOrder(userId, orderId));
    }
}