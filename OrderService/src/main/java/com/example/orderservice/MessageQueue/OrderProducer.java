package com.example.orderservice.MessageQueue;

import com.example.orderservice.dto.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;


@Service
@Slf4j
@AllArgsConstructor
public class OrderProducer {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final List<Field> fields = Arrays.asList(new Field("string", true, "order_id"),
            new Field("string", true, "user_id"),
            new Field("string", true, "product_id"),
            new Field("int32", true, "qty"),
            new Field("int32", true, "unit_price"),
            new Field("int32", true, "total_price"));
    private  final Schema schema = Schema.builder()
            .type("struct")
            .fields(fields)
            .optional(false)
            .name("orders")
            .build();


    public OrderDto send(String topic, OrderDto orderDto){

        Payload payload =Payload.builder()
                .order_id(orderDto.getOrderId())
                .user_id(orderDto.getUserId())
                .product_id(orderDto.getProductId())
                .qty(orderDto.getQty())
                .unit_price(orderDto.getUnitPrice())
                .total_price(orderDto.getTotalPrice())
                .build();

        KafkaOrderDto kafkaOrderDto = new KafkaOrderDto(schema,payload);

        ObjectMapper objectMapper = new ObjectMapper();
        String JsonInString ="";

        try{
            JsonInString=objectMapper.writeValueAsString(kafkaOrderDto);
        }
        catch (Exception e){
            e.printStackTrace();
        }
        kafkaTemplate.send(topic, JsonInString);
        log.info("kafka producer send data from order ms :{}",kafkaOrderDto);

        return orderDto;
    }


}
