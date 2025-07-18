package com.ecommerce.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class OrderDTO {
    public Long id;
    public Long userId;
    public String userName;
    public LocalDateTime orderDate;
    public BigDecimal total;
    public String status;
    public List<OrderItemDTO> items;
    
    public OrderDTO() {}
    
    public OrderDTO(Long id, Long userId, String userName, LocalDateTime orderDate, BigDecimal total, String status, List<OrderItemDTO> items) {
        this.id = id;
        this.userId = userId;
        this.userName = userName;
        this.orderDate = orderDate;
        this.total = total;
        this.status = status;
        this.items = items;
    }
}