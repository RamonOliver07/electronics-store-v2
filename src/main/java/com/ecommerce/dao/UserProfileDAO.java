package com.ecommerce.dao;

import com.ecommerce.entity.UserProfile;
import io.quarkus.hibernate.orm.panache.PanacheRepository;

import javax.enterprise.context.ApplicationScoped;
import java.util.Optional;

@ApplicationScoped
public class UserProfileDAO implements PanacheRepository<UserProfile> {
    
    public Optional<UserProfile> findByName(String name) {
        return find("name = ?1", name).firstResultOptional();
    }
}