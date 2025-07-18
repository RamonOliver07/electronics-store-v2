package com.ecommerce.dto;

import java.util.List;

public class CreateOrderDTO {
    public List<CreateOrderItemDTO> items;
    
    public CreateOrderDTO() {}
    
    public CreateOrderDTO(List<CreateOrderItemDTO> items) {
        this.items = items;
    }
}