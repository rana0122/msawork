package com.example.orderservice.MessageQueue;

import com.example.orderservice.dto.OrderDto;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;


@Service
@Slf4j
@AllArgsConstructor
public class KafkaProducer {

    private final KafkaTemplate<String, String> kafkaTemplate;

    public OrderDto send(String topic, OrderDto orderDto){
        ObjectMapper objectMapper = new ObjectMapper();
        String JsonInString ="";

        try{
            JsonInString=objectMapper.writeValueAsString(orderDto);
        }
        catch (Exception e){
            e.printStackTrace();
        }
        kafkaTemplate.send(topic, JsonInString);
        log.info("kafka producer send data from order ms :{}",orderDto);
        return orderDto;
    }


}
