package com.ecommerce.dto;

import java.math.BigDecimal;

public class OrderItemDTO {
    public Long id;
    public Long productId;
    public String productName;
    public Integer quantity;
    public BigDecimal price;
    
    public OrderItemDTO() {}
    
    public OrderItemDTO(Long id, Long productId, String productName, Integer quantity, BigDecimal price) {
        this.id = id;
        this.productId = productId;
        this.productName = productName;
        this.quantity = quantity;
        this.price = price;
    }
}