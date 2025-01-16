package com.example.orderservice.Service;

import com.example.orderservice.JPA.OrderEntity;
import com.example.orderservice.dto.OrderDto;

public interface OrderService {

    OrderDto createOrder(OrderDto orderDetails);
    OrderDto getOrderById(String orderId);
    Iterable<OrderEntity> getAllOrdersByUserId(String userId);
}
