package com.ecommerce.dao;

import com.ecommerce.entity.User;
import io.quarkus.hibernate.orm.panache.PanacheRepository;

import javax.enterprise.context.ApplicationScoped;
import java.util.Optional;

@ApplicationScoped
public class UserDAO implements PanacheRepository<User> {
    
    public Optional<User> findByEmail(String email) {
        return find("email = ?1 and active = true", email).firstResultOptional();
    }
    
    public boolean existsByEmail(String email) {
        return count("email = ?1", email) > 0;
    }
}