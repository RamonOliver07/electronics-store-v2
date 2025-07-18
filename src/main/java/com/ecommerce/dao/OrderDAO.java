package com.ecommerce.dao;

import com.ecommerce.entity.Order;
import com.ecommerce.entity.User;
import io.quarkus.hibernate.orm.panache.PanacheRepository;

import javax.enterprise.context.ApplicationScoped;
import java.util.List;

@ApplicationScoped
public class OrderDAO implements PanacheRepository<Order> {
    
    public List<Order> findByUser(User user) {
        return list("user = ?1 order by orderDate desc", user);
    }
    
    public List<Order> findAll() {
        return list("order by orderDate desc");
    }
}