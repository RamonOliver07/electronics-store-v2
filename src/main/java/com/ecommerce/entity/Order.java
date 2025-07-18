package com.ecommerce.entity;

import io.quarkus.hibernate.orm.panache.PanacheEntity;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "orders")
public class Order extends PanacheEntity {
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    public User user;
    
    @Column(name = "order_date")
    public LocalDateTime orderDate = LocalDateTime.now();
    
    @Column(nullable = false, precision = 10, scale = 2)
    public BigDecimal total;
    
    @Enumerated(EnumType.STRING)
    public OrderStatus status = OrderStatus.PENDING;
    
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    public List<OrderItem> items;
    
    public Order() {}
    
    public Order(User user, BigDecimal total) {
        this.user = user;
        this.total = total;
    }
    
    public enum OrderStatus {
        PENDING, CONFIRMED, SHIPPED, DELIVERED, CANCELLED
    }
}