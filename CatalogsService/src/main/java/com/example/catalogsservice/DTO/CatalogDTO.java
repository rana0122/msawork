package com.example.catalogsservice.DTO;

import lombok.Data;

import java.io.Serializable;

@Data
public class CatalogDTO implements Serializable {

    private String productId;
    private Integer qty;
    private Integer unitPrice;
    private Integer totalPrice;

    private String orderId;
    private String userId;
}
