package com.ecommerce.dto;

public class UserDTO {
    public Long id;
    public String email;
    public String name;
    public Boolean active;
    public String profileName;
    
    public UserDTO() {}
    
    public UserDTO(Long id, String email, String name, Boolean active, String profileName) {
        this.id = id;
        this.email = email;
        this.name = name;
        this.active = active;
        this.profileName = profileName;
    }
}