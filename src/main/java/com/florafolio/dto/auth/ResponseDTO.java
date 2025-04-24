package com.florafolio.dto.auth;


public class ResponseDTO {
    private String status;
    private String message;
    
    // Constructors
    public ResponseDTO() {}
    
    public ResponseDTO(String status, String message) {
        this.status = status;
        this.message = message;
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
}