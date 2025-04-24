package com.florafolio.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Column;
import jakarta.persistence.Lob;

import java.util.UUID;

@Entity
@Table(name = "plants")
public class Plant {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    
    @Column(nullable = false)
    private String popularName;
    
    @Column(nullable = false)
    private String scientificName;
    
    @Lob
    @Column(columnDefinition = "TEXT")
    private String description;
    
    @Column
    private String family;
    
    @Column
    private String origin;
    
    @Column
    private String careInstructions;
    
    @Column
    private String imageUrl;
    
    // Construtores
    public Plant() {}
    
    public Plant(UUID id, String popularName, String scientificName, String description, 
                String family, String origin, String careInstructions, String imageUrl) {
        this.id = id;
        this.popularName = popularName;
        this.scientificName = scientificName;
        this.description = description;
        this.family = family;
        this.origin = origin;
        this.careInstructions = careInstructions;
        this.imageUrl = imageUrl;
    }
    
    // Getters e Setters
    public UUID getId() {
        return id;
    }
    
    public void setId(UUID id) {
        this.id = id;
    }
    
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