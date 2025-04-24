package com.florafolio.dto.plant;

import java.util.List;

public class PlantResponseDTO {
    private String status;
    private String message;
    private List<PlantDTO> plants;
    
    // Construtores
    public PlantResponseDTO() {}
    
    public PlantResponseDTO(String status, String message, List<PlantDTO> plants) {
        this.status = status;
        this.message = message;
        this.plants = plants;
    }
    
    // Getters e Setters
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
    
    public List<PlantDTO> getPlants() {
        return plants;
    }
    
    public void setPlants(List<PlantDTO> plants) {
        this.plants = plants;
    }
}