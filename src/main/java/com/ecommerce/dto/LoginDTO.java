package com.ecommerce.dto;

public class LoginDTO {
    public String email;
    public String password;
    
    public LoginDTO() {}
    
    public LoginDTO(String email, String password) {
        this.email = email;
        this.password = password;
    }
}