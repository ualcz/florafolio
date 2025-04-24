package com.florafolio.dto.auth;
import jakarta.validation.constraints.NotBlank;

public class PasswordUpdateDTO {
    @NotBlank(message = "A senha atualizada nao pode estar vazia")
    private String currentPassword;

    @NotBlank(message = "A nova senha nao pode estar vazia")    
    private String newPassword;
    
    // Constructors
    public PasswordUpdateDTO() {}
    
    public PasswordUpdateDTO(String currentPassword, String newPassword) {
        this.currentPassword = currentPassword;
        this.newPassword = newPassword;
    }
    
    // Getters and Setters
    public String getCurrentPassword() {
        return currentPassword;
    }
    
    public void setCurrentPassword(String currentPassword) {
        this.currentPassword = currentPassword;
    }
    
    public String getNewPassword() {
        return newPassword;
    }
    
    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }
}