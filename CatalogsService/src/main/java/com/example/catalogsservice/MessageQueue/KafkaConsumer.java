package com.example.catalogsservice.MessageQueue;

import com.example.catalogsservice.JPA.CatalogEntity;
import com.example.catalogsservice.JPA.CatalogRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
@AllArgsConstructor
public class KafkaConsumer {

    private final CatalogRepository catalogRepository;

    @KafkaListener(topics = "example-catalog-topic")
    public void updateQty(String message) {
        log.info("kafka message ->  {}", message);

        Map<Object,Object> map = new HashMap<>();
        ObjectMapper objectMapper = new ObjectMapper();

        try{
            map = objectMapper.readValue(message, new TypeReference<Map<Object, Object>>() {});
        }catch (JsonProcessingException e){
            e.printStackTrace();
        }

        CatalogEntity entity = catalogRepository.findByProductId(map.get("productId").toString());

        if(entity != null){
            entity.setStock(entity.getStock()- (Integer)map.get("qty"));
            catalogRepository.save(entity);
        }

    }
}
