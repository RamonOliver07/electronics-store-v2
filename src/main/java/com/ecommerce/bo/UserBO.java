package com.ecommerce.bo;

import com.ecommerce.dao.UserDAO;
import com.ecommerce.dao.UserProfileDAO;
import com.ecommerce.dto.CreateUserDTO;
import com.ecommerce.dto.LoginDTO;
import com.ecommerce.dto.UserDTO;
import com.ecommerce.entity.User;
import com.ecommerce.entity.UserProfile;
import io.smallrye.jwt.build.Jwt;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;
import java.security.MessageDigest;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@ApplicationScoped
public class UserBO {
    
    @Inject
    UserDAO userDAO;
    
    @Inject
    UserProfileDAO userProfileDAO;
    
    @Transactional
    public UserDTO createUser(CreateUserDTO createUserDTO) {
        validateCreateUser(createUserDTO);
        
        Optional<UserProfile> profileOpt = userProfileDAO.findByIdOptional(createUserDTO.userProfileId);
        if (profileOpt.isEmpty()) {
            throw new RuntimeException("User profile not found");
        }
        
        String hashedPassword = hashPassword(createUserDTO.password);
        User user = new User(createUserDTO.email, hashedPassword, createUserDTO.name, profileOpt.get());
        userDAO.persist(user);
        
        return convertToDTO(user);
    }
    
    public String authenticate(LoginDTO loginDTO) {
        validateLogin(loginDTO);
        
        Optional<User> userOpt = userDAO.findByEmail(loginDTO.email);
        if (userOpt.isEmpty()) {
            throw new RuntimeException("Invalid credentials");
        }
        
        User user = userOpt.get();
        if (!verifyPassword(loginDTO.password, user.password)) {
            throw new RuntimeException("Invalid credentials");
        }
        
        return generateJWT(user);
    }
    
    public List<UserDTO> getAllUsers() {
        return userDAO.listAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    public Optional<UserDTO> getUserById(Long id) {
        return userDAO.findByIdOptional(id)
                .map(this::convertToDTO);
    }
    
    @Transactional
    public void deactivateUser(Long id) {
        Optional<User> userOpt = userDAO.findByIdOptional(id);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            user.active = false;
            userDAO.persist(user);
        }
    }
    
    private void validateCreateUser(CreateUserDTO dto) {
        if (dto.email == null || dto.email.trim().isEmpty()) {
            throw new RuntimeException("Email is required");
        }
        if (dto.password == null || dto.password.trim().isEmpty()) {
            throw new RuntimeException("Password is required");
        }
        if (dto.name == null || dto.name.trim().isEmpty()) {
            throw new RuntimeException("Name is required");
        }
        if (userDAO.existsByEmail(dto.email)) {
            throw new RuntimeException("Email already exists");
        }
    }
    
    private void validateLogin(LoginDTO dto) {
        if (dto.email == null || dto.email.trim().isEmpty()) {
            throw new RuntimeException("Email is required");
        }
        if (dto.password == null || dto.password.trim().isEmpty()) {
            throw new RuntimeException("Password is required");
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
    
    private boolean verifyPassword(String password, String hashedPassword) {
        return hashPassword(password).equals(hashedPassword);
    }
    
    private String generateJWT(User user) {
        return Jwt.issuer("https://electronics-store.com")
                .upn(user.email)
                .subject(user.id.toString())
                .claim("name", user.name)
                .claim("profile", user.userProfile.name)
                .expiresAt(System.currentTimeMillis() + 3600)
                .sign();
    }
    
    private UserDTO convertToDTO(User user) {
        return new UserDTO(user.id, user.email, user.name, user.active, user.userProfile.name);
    }
}