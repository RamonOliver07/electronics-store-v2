package com.ecommerce.dao;

import com.ecommerce.entity.Order;
import com.ecommerce.entity.User;
import io.quarkus.hibernate.orm.panache.PanacheRepository;

import javax.enterprise.context.ApplicationScoped;
import java.util.List;

@ApplicationScoped
public class OrderDAO implements PanacheRepository<Order> {
    
    public List<Order> findByUser(User user) {
        // Esta query está correta, busca os pedidos de um usuário específico e ordena
        return list("user = ?1 order by orderDate desc", user);
    }
    
    // MUDANÇA: Renomeamos o método para não conflitar com o findAll() do Panache
    public List<Order> findAllOrderedByDate() {
        // Esta query busca TODOS os pedidos e ordena
        return list("order by orderDate desc");
    }
}
