package com.florafolio.service;

import com.florafolio.model.Plant;
import com.florafolio.repository.PlantRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class PlantService {
    @Autowired
    private PlantRepository plantRepository;
    
    @PostConstruct
    @Transactional
    public void init() {
        // Adiciona algumas plantas para teste se o banco estiver vazio
        if (plantRepository.count() == 0) {
            // Planta 1
            Plant plant1 = new Plant(
                null,
                "Samambaia",
                "Nephrolepis exaltata",
                "A samambaia é uma planta ornamental muito popular, conhecida por suas folhas verdes e delicadas.",
                "Nephrolepidaceae",
                "América Central e do Sul",
                "Manter em local com luz indireta e solo úmido. Regar regularmente.",
                "https://example.com/samambaia.jpg"
            );
            
            // Planta 2
            Plant plant2 = new Plant(
                null,
                "Espada de São Jorge",
                "Sansevieria trifasciata",
                "Planta resistente com folhas eretas e pontiagudas, excelente para purificar o ar.",
                "Asparagaceae",
                "África Ocidental",
                "Tolera baixa luminosidade e pouca água. Regar apenas quando o solo estiver seco.",
                "https://example.com/espada-sao-jorge.jpg"
            );
            
            // Planta 3
            Plant plant3 = new Plant(
                null,
                "Orquídea Phalaenopsis",
                "Phalaenopsis spp.",
                "Orquídea popular com flores duradouras e elegantes em diversos tons.",
                "Orchidaceae",
                "Sudeste Asiático",
                "Luz indireta, regar quando o substrato estiver seco, ambiente úmido.",
                "https://example.com/orquidea.jpg"
            );
            
            plantRepository.save(plant1);
            plantRepository.save(plant2);
            plantRepository.save(plant3);
        }
    }
    
    // Buscar todas as plantas
    public List<Plant> getAllPlants() {
        return plantRepository.findAll();
    }
    
    // Buscar planta por ID
    public Plant getPlantById(UUID id) {
        return plantRepository.findById(id).orElse(null);
    }
    
    // Buscar plantas por nome popular
    public List<Plant> getPlantsByPopularName(String popularName) {
        return plantRepository.findByPopularNameContainingIgnoreCase(popularName);
    }
    
    // Buscar plantas por nome científico
    public List<Plant> getPlantsByScientificName(String scientificName) {
        return plantRepository.findByScientificNameContainingIgnoreCase(scientificName);
    }
    
    // Buscar plantas por nome popular ou científico
    public List<Plant> searchPlants(String term) {
        return plantRepository.findByPopularNameContainingIgnoreCaseOrScientificNameContainingIgnoreCase(term, term);
    }
    
    // Criar nova planta
    @Transactional
    public Plant createPlant(Plant plant) {
        return plantRepository.save(plant);
    }
    
    // Atualizar planta existente
    @Transactional
    public Plant updatePlant(UUID id, Plant plantDetails) {
        Plant plant = plantRepository.findById(id).orElse(null);
        if (plant != null) {
            // Atualiza apenas os campos não nulos
            if (plantDetails.getPopularName() != null) {
                plant.setPopularName(plantDetails.getPopularName());
            }
            if (plantDetails.getScientificName() != null) {
                plant.setScientificName(plantDetails.getScientificName());
            }
            if (plantDetails.getDescription() != null) {
                plant.setDescription(plantDetails.getDescription());
            }
            if (plantDetails.getFamily() != null) {
                plant.setFamily(plantDetails.getFamily());
            }
            if (plantDetails.getOrigin() != null) {
                plant.setOrigin(plantDetails.getOrigin());
            }
            if (plantDetails.getCareInstructions() != null) {
                plant.setCareInstructions(plantDetails.getCareInstructions());
            }
            if (plantDetails.getImageUrl() != null) {
                plant.setImageUrl(plantDetails.getImageUrl());
            }
            return plantRepository.save(plant);
        }
        return null;
    }
    
    // Excluir planta
    @Transactional
    public boolean deletePlant(UUID id) {
        Plant plant = plantRepository.findById(id).orElse(null);
        if (plant != null) {
            plantRepository.delete(plant);
            return true;
        }
        return false;
    }
}