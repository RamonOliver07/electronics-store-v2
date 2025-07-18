package com.ecommerce.entity;

import io.quarkus.hibernate.orm.panache.PanacheEntity;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "user_profiles")
public class UserProfile extends PanacheEntity {
    
    @Column(unique = true, nullable = false)
    public String name;
    
    public String description;
    
    @Column(name = "can_manage_products")
    public Boolean canManageProducts = false;
    
    @Column(name = "can_manage_orders")
    public Boolean canManageOrders = false;
    
    @Column(name = "can_manage_users")
    public Boolean canManageUsers = false;
    
    @Column(name = "can_place_orders")
    public Boolean canPlaceOrders = true;
    
    @OneToMany(mappedBy = "userProfile", cascade = CascadeType.ALL)
    public List<User> users;
    
    public UserProfile() {}
    
    public UserProfile(String name, String description) {
        this.name = name;
        this.description = description;
    }
}