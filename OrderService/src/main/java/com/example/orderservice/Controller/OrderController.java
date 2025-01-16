package com.example.orderservice.Controller;

import com.example.orderservice.JPA.OrderEntity;
import com.example.orderservice.MessageQueue.KafkaProducer;
import com.example.orderservice.MessageQueue.OrderProducer;
import com.example.orderservice.Service.OrderService;
import com.example.orderservice.dto.OrderDto;
import com.example.orderservice.VO.RequestOrder;
import com.example.orderservice.VO.ResponseOrder;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RestController
@AllArgsConstructor
@RequestMapping("/order-service")
public class OrderController {

    private final Environment environment;
    private final OrderService orderService;
    private final KafkaProducer kafkaProducer;
    private final OrderProducer orderProducer;

    @GetMapping("/health_check")
    public String status(){
        return String.format("It's working in OrderService on Port %s",environment.getProperty("local.server.port"));
    }

    @PostMapping("/{userId}/orders")
    public ResponseEntity<ResponseOrder> createOrder(@PathVariable("userId") String userId,
                                                     @RequestBody RequestOrder orderDetails) {
        ModelMapper modelMapper = new ModelMapper();
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        OrderDto orderDto = modelMapper.map(orderDetails, OrderDto.class);
        orderDto.setUserId(userId);

//        JPA
//        OrderDto createdOrder = orderService.createOrder(orderDto);
//        ResponseOrder responseOrder = modelMapper.map(createdOrder, ResponseOrder.class);
        /* kafka */
        orderDto.setOrderId(UUID.randomUUID().toString());
        orderDto.setTotalPrice(orderDetails.getQty() * orderDetails.getUnitPrice());

//        send this order to the kafka
        kafkaProducer.send("example-catalog-topic", orderDto);
        orderProducer.send("orders", orderDto);

        ResponseOrder responseOrder = modelMapper.map(orderDto, ResponseOrder.class);
        return ResponseEntity.status(HttpStatus.CREATED).body(responseOrder);
    }

    @GetMapping("/{userId}/orders")
    public ResponseEntity<List<ResponseOrder>> getOrders(@PathVariable("userId") String userId){
        Iterable<OrderEntity> orderList = orderService.getAllOrdersByUserId(userId);
        List<ResponseOrder> responseOrders = new ArrayList<>();

        orderList.forEach(v-> {
            responseOrders.add(new ModelMapper().map(v, ResponseOrder.class));
        });

        return ResponseEntity.status(HttpStatus.OK).body(responseOrders);
    }
}
