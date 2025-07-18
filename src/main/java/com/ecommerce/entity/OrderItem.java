package com.ecommerce.entity;

import io.quarkus.hibernate.orm.panache.PanacheEntity;

import javax.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "order_items")
public class OrderItem extends PanacheEntity {
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    public Order order;
    
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "product_id")
    public Product product;
    
    @Column(nullable = false)
    public Integer quantity;
    
    @Column(nullable = false, precision = 10, scale = 2)
    public BigDecimal price;
    
    public OrderItem() {}
    
    public OrderItem(Order order, Product product, Integer quantity, BigDecimal price) {
        this.order = order;
        this.product = product;
        this.quantity = quantity;
        this.price = price;
    }
}