package com.ecommerce.entity;

import io.quarkus.hibernate.orm.panache.PanacheEntity;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "users")
public class User extends PanacheEntity {
    
    @Column(unique = true, nullable = false)
    public String email;
    
    @Column(nullable = false)
    public String password;
    
    @Column(nullable = false)
    public String name;
    
    @Column(nullable = false)
    public Boolean active = true;
    
    @Column(name = "created_at")
    public LocalDateTime createdAt = LocalDateTime.now();
    
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_profile_id")
    public UserProfile userProfile;
    
    public User() {}
    
    public User(String email, String password, String name, UserProfile userProfile) {
        this.email = email;
        this.password = password;
        this.name = name;
        this.userProfile = userProfile;
    }
}