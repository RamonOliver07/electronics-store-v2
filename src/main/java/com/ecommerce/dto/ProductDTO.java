package com.ecommerce.dto;

import java.math.BigDecimal;

public class ProductDTO {
    public Long id;
    public String name;
    public String description;
    public BigDecimal price;
    public Integer stock;
    public String category;
    public String brand;
    public String imageUrl;
    public Boolean active;
    
    public ProductDTO() {}
    
    public ProductDTO(Long id, String name, String description, BigDecimal price, Integer stock, String category, String brand, String imageUrl, Boolean active) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.price = price;
        this.stock = stock;
        this.category = category;
        this.brand = brand;
        this.imageUrl = imageUrl;
        this.active = active;
    }
}