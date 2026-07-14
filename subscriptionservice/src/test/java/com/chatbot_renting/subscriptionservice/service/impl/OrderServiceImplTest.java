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
        Order order = Order.builder().id(1L).build();
        Page<Order> orderPage = new PageImpl<>(Collections.singletonList(order));
        
        when(orderRepository.findByUserIdAndStatusOrderByCreatedAtDesc(eq(1L), eq(OrderStatus.PAID), any(PageRequest.class)))
                .thenReturn(orderPage);
        
        OrderDto orderDto = new OrderDto();
        orderDto.setId(1L);
        when(orderMapper.toDto(any(Order.class))).thenReturn(orderDto);
        
        // Act
        PagedResponse<OrderDto> result = orderService.getUserOrders(1L, 1, 10, "PAID");
        
        // Assert
        assertNotNull(result);
        assertEquals(1, result.getData().size());
        assertEquals(1L, result.getData().get(0).getId());
    }

    @Test
    void getOrder_Exists_ReturnsDto() {
        // Arrange
        Order order = Order.builder().id(2L).userId(1L).build();
        when(orderRepository.findById(2L)).thenReturn(Optional.of(order));
        
        OrderDto orderDto = new OrderDto();
        orderDto.setId(2L);
        when(orderMapper.toDto(order)).thenReturn(orderDto);
        
        // Act
        OrderDto result = orderService.getOrder(1L, 2L);
        
        // Assert
        assertNotNull(result);
        assertEquals(2L, result.getId());
    }

    @Test
    void getOrder_NotFound_ThrowsException() {
        // Arrange
        when(orderRepository.findById(2L)).thenReturn(Optional.empty());
        
        // Act & Assert
        assertThrows(AppNotFoundException.class, () -> orderService.getOrder(1L, 2L));
    }
}
