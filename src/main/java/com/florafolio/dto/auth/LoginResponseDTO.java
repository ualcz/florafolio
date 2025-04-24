package com.florafolio.dto.auth;

public class LoginResponseDTO {
    private String status;
    private String message;
    private String username;
    private String token;
    
    // Constructors
    public LoginResponseDTO() {}
    
    public LoginResponseDTO(String status, String message, String username, String token) {
        this.status = status;
        this.message = message;
        this.username = username;
        this.token = token;
    }
    
    // Getters and Setters
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }
    
    public String getMessage() {
        return message;
    }
    
    public void setMessage(String message) {
        this.message = message;
    }
    
    public String getUsername() {
        return username;
    }
    
    public void setUsername(String username) {
        this.username = username;
    }
    
    public String getToken() {
        return token;
    }
    
    public void setToken(String token) {
        this.token = token;
    }
}