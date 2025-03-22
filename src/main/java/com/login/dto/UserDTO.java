package com.login.dto;

import java.util.UUID;
import jakarta.validation.constraints.NotBlank;

public class UserDTO {
    private UUID id;
    @NotBlank(message = "Username is required")
    private String username;
    private String email;
    private boolean isOwnProfile;
    
    // Constructors
    public UserDTO() {}
    
    public UserDTO(UUID id, String username, String email, boolean isOwnProfile) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.isOwnProfile = isOwnProfile;
    }

    public UserDTO(String username, boolean isOwnProfile) {
        this.username = username;
        this.isOwnProfile = isOwnProfile;
    }
    
    // Getters and Setters
    public UUID getId() {
        return id;
    }
    
    public void setId(UUID id) {
        this.id = id;
    }
    
    public String getUsername() {
        return username;
    }
    
    public void setUsername(String username) {
        this.username = username;
    }
    
    public String getEmail() {
        // Only return email if this is the user's own profile
        return isOwnProfile ? email : null;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
    
    public boolean isOwnProfile() {
        return isOwnProfile;
    }
    
    public void setOwnProfile(boolean isOwnProfile) {
        this.isOwnProfile = isOwnProfile;
    }
}