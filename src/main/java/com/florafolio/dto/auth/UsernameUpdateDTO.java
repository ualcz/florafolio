package com.florafolio.dto.auth;
import jakarta.validation.constraints.NotBlank;

public class UsernameUpdateDTO {
    @NotBlank(message = "O nome de usuário atualizado não pode estar vazio")
    private String currentUsername;

    @NotBlank(message = "O novo nome de usuário não pode estar vazio")
    private String newUsername;
    
    // Constructors
    public UsernameUpdateDTO() {}
    
    public UsernameUpdateDTO(String currentUsername, String newUsername) {
        this.currentUsername = currentUsername;
        this.newUsername = newUsername;
    }
    
    // Getters and Setters
    public String getCurrentUsername() {
        return currentUsername;
    }
    
    public void setCurrentUsername(String currentUsername) {
        this.currentUsername = currentUsername;
    }
    
    public String getNewUsername() {
        return newUsername;
    }
    
    public void setNewUsername(String newUsername) {
        this.newUsername = newUsername;
    }
}
