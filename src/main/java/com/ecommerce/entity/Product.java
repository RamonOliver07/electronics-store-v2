package com.ecommerce.entity;

import io.quarkus.hibernate.orm.panache.PanacheEntity;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "products")
public class Product extends PanacheEntity {
    
    @Column(nullable = false)
    public String name;
    
    @Column(columnDefinition = "TEXT")
    public String description;
    
    @Column(nullable = false, precision = 10, scale = 2)
    public BigDecimal price;
    
    @Column(nullable = false)
    public Integer stock = 0;
    
    @Column(nullable = false)
    public String category;
    
    public String brand;
    
    @Column(name = "image_url")
    public String imageUrl;
    
    @Column(nullable = false)
    public Boolean active = true;
    
    @Column(name = "created_at")
    public LocalDateTime createdAt = LocalDateTime.now();
    
    public Product() {}
    
    public Product(String name, String description, BigDecimal price, Integer stock, String category, String brand, String imageUrl) {
        this.name = name;
        this.description = description;
        this.price = price;
        this.stock = stock;
        this.category = category;
        this.brand = brand;
        this.imageUrl = imageUrl;
    }
}