package com.example.orderservice.Service;

import com.example.orderservice.JPA.OrderEntity;
import com.example.orderservice.JPA.OrderRepository;
import com.example.orderservice.dto.OrderDto;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@AllArgsConstructor
public class OrderServiceImpl implements OrderService{

    private final OrderRepository orderRepository;

    @Override
    public OrderDto createOrder(OrderDto orderDetails) {
        orderDetails.setOrderId(UUID.randomUUID().toString());
        orderDetails.setTotalPrice(orderDetails.getQty() * orderDetails.getUnitPrice());
        ModelMapper modelMapper = new ModelMapper();
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        OrderEntity order = modelMapper.map(orderDetails, OrderEntity.class);
        orderRepository.save(order);
        OrderDto orderDto = modelMapper.map(order, OrderDto.class);
        return orderDto;
    }

    @Override
    public OrderDto getOrderById(String orderId) {
        OrderEntity order = orderRepository.findByOrderId(orderId);
        OrderDto orderDto = new ModelMapper().map(order, OrderDto.class);
        return orderDto;
    }

    @Override
    public Iterable<OrderEntity> getAllOrdersByUserId(String userId) {
        return orderRepository.findAllByUserId(userId);
    }
}
