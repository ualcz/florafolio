package com.florafolio.dto.plant;

import jakarta.validation.constraints.Size;

public class UpdatePlantRequestDTO {
    @Size(min = 2, max = 100, message = "O nome popular deve ter entre 2 e 100 caracteres")
    private String popularName;
    
    @Size(min = 2, max = 100, message = "O nome científico deve ter entre 2 e 100 caracteres")
    private String scientificName;
    
    private String description;
    private String family;
    private String origin;
    private String careInstructions;
    private String imageUrl;
    
    // Construtores
    public UpdatePlantRequestDTO() {}
    
    public UpdatePlantRequestDTO(String popularName, String scientificName, String description, 
                   String family, String origin, String careInstructions, String imageUrl) {
        this.popularName = popularName;
        this.scientificName = scientificName;
        this.description = description;
        this.family = family;
        this.origin = origin;
        this.careInstructions = careInstructions;
        this.imageUrl = imageUrl;
    }
    
    // Getters e Setters
    public String getPopularName() {
        return popularName;
    }
    
    public void setPopularName(String popularName) {
        this.popularName = popularName;
    }
    
    public String getScientificName() {
        return scientificName;
    }
    
    public void setScientificName(String scientificName) {
        this.scientificName = scientificName;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public String getFamily() {
        return family;
    }
    
    public void setFamily(String family) {
        this.family = family;
    }
    
    public String getOrigin() {
        return origin;
    }
    
    public void setOrigin(String origin) {
        this.origin = origin;
    }
    
    public String getCareInstructions() {
        return careInstructions;
    }
    
    public void setCareInstructions(String careInstructions) {
        this.careInstructions = careInstructions;
    }
    
    public String getImageUrl() {
        return imageUrl;
    }
    
    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}