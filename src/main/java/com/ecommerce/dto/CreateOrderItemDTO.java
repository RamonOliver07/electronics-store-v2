package com.ecommerce.dto;

public class CreateOrderItemDTO {
    public Long productId;
    public Integer quantity;
    
    public CreateOrderItemDTO() {}
    
    public CreateOrderItemDTO(Long productId, Integer quantity) {
        this.productId = productId;
        this.quantity = quantity;
    }
}