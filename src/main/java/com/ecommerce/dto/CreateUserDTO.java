package com.ecommerce.dto;

public class CreateUserDTO {
    public String email;
    public String password;
    public String name;
    public Long userProfileId;
    
    public CreateUserDTO() {}
    
    public CreateUserDTO(String email, String password, String name, Long userProfileId) {
        this.email = email;
        this.password = password;
        this.name = name;
        this.userProfileId = userProfileId;
    }
}