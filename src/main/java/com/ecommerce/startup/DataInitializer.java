package com.ecommerce.startup;

import com.ecommerce.entity.UserProfile;
import com.ecommerce.entity.User;
import com.ecommerce.entity.Product;
import io.quarkus.runtime.StartupEvent;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.security.MessageDigest;

@ApplicationScoped
public class DataInitializer {
    
    @Transactional
    void onStart(@Observes StartupEvent ev) {
        // Create user profiles
        UserProfile adminProfile = new UserProfile("ADMIN", "Administrator with full access");
        adminProfile.canManageProducts = true;
        adminProfile.canManageOrders = true;
        adminProfile.canManageUsers = true;
        adminProfile.canPlaceOrders = true;
        adminProfile.persist();
        
        UserProfile userProfile = new UserProfile("USER", "Regular user");
        userProfile.canPlaceOrders = true;
        userProfile.persist();
        
        // Create admin user
        User admin = new User("admin@ecommerce.com", hashPassword("admin123"), "Administrator", adminProfile);
        admin.persist();
        
        // Create regular user
        User user = new User("user@ecommerce.com", hashPassword("user123"), "Regular User", userProfile);
        user.persist();
        
        // Create sample products
        createSampleProducts();
    }
    
    private void createSampleProducts() {
        Product[] products = {
            new Product("iPhone 14 Pro", "Latest Apple iPhone with Pro camera system", 
                new BigDecimal("999.99"), 10, "Smartphones", "Apple",
                "https://images.pexels.com/photos/4498362/pexels-photo-4498362.jpeg"),
            
            new Product("Samsung Galaxy S23 Ultra", "Premium Android smartphone with S Pen", 
                new BigDecimal("1199.99"), 8, "Smartphones", "Samsung",
                "https://images.pexels.com/photos/5985611/pexels-photo-5985611.jpeg"),
            
            new Product("MacBook Pro 16\"", "Powerful laptop for professionals", 
                new BigDecimal("2499.99"), 5, "Laptops", "Apple",
                "https://images.pexels.com/photos/303383/pexels-photo-303383.jpeg"),
            
            new Product("Dell XPS 13", "Compact and powerful ultrabook", 
                new BigDecimal("1299.99"), 7, "Laptops", "Dell",
                "https://images.pexels.com/photos/1229861/pexels-photo-1229861.jpeg"),
            
            new Product("Sony WH-1000XM4", "Premium noise-cancelling headphones", 
                new BigDecimal("349.99"), 15, "Audio", "Sony",
                "https://images.pexels.com/photos/3394650/pexels-photo-3394650.jpeg"),
            
            new Product("iPad Air", "Versatile tablet for work and entertainment", 
                new BigDecimal("599.99"), 12, "Tablets", "Apple",
                "https://images.pexels.com/photos/1334597/pexels-photo-1334597.jpeg"),
            
            new Product("Nintendo Switch", "Popular gaming console", 
                new BigDecimal("299.99"), 20, "Gaming", "Nintendo",
                "https://images.pexels.com/photos/1174746/pexels-photo-1174746.jpeg"),
            
            new Product("AirPods Pro", "Wireless earbuds with active noise cancellation", 
                new BigDecimal("249.99"), 25, "Audio", "Apple",
                "https://images.pexels.com/photos/3780681/pexels-photo-3780681.jpeg")
        };
        
        for (Product product : products) {
            product.persist();
        }
    }
    
    private String hashPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hashedBytes = md.digest(password.getBytes());
            StringBuilder sb = new StringBuilder();
            for (byte b : hashedBytes) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (Exception e) {
            throw new RuntimeException("Error hashing password", e);
        }
    }
}