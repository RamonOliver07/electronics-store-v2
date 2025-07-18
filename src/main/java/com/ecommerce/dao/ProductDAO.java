package com.ecommerce.dao;

import com.ecommerce.entity.Product;
import io.quarkus.hibernate.orm.panache.PanacheRepository;

import javax.enterprise.context.ApplicationScoped;
import java.util.List;

@ApplicationScoped
public class ProductDAO implements PanacheRepository<Product> {
    
    public List<Product> findActiveProducts() {
        return list("active = true order by name");
    }
    
    public List<Product> findByCategory(String category) {
        return list("category = ?1 and active = true order by name", category);
    }
    
    public List<Product> searchByName(String name) {
        return list("LOWER(name) LIKE LOWER(?1) and active = true order by name", "%" + name + "%");
    }
}