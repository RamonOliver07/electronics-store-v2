package com.ecommerce.dto;

import java.math.BigDecimal;

public class CreateProductDTO {
    public String name;
    public String description;
    public BigDecimal price;
    public Integer stock;
    public String category;
    public String brand;
    public String imageUrl;
    
    public CreateProductDTO() {}
    
    public CreateProductDTO(String name, String description, BigDecimal price, Integer stock, String category, String brand, String imageUrl) {
        this.name = name;
        this.description = description;
        this.price = price;
        this.stock = stock;
        this.category = category;
        this.brand = brand;
        this.imageUrl = imageUrl;
    }
}