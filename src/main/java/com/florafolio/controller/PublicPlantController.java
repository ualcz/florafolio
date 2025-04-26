package com.florafolio.controller;

import com.florafolio.dto.plant.PlantDTO;
import com.florafolio.dto.plant.PlantResponseDTO;
import com.florafolio.model.Plant;
import com.florafolio.service.PlantService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/plants")
public class PublicPlantController {
    
    @Autowired
    private PlantService plantService;
    
    // Converter Plant para PlantDTO
    private PlantDTO convertToDTO(Plant plant) {
        return new PlantDTO(
            plant.getId(),
            plant.getPopularName(),
            plant.getScientificName(),
            plant.getDescription(),
            plant.getFamily(),
            plant.getOrigin(),
            plant.getCareInstructions(),
            plant.getImageUrl()
        );
    }
    
    // Converter lista de Plant para lista de PlantDTO
    private List<PlantDTO> convertToDTOList(List<Plant> plants) {
        List<PlantDTO> plantDTOs = new ArrayList<>();
        for (Plant plant : plants) {
            plantDTOs.add(convertToDTO(plant));
        }
        return plantDTOs;
    }
    
    // Endpoint para listar todas as plantas (acessível a todos)
    @GetMapping
    public ResponseEntity<PlantResponseDTO> getAllPlants() {
        List<Plant> plants = plantService.getAllPlants();
        List<PlantDTO> plantDTOs = convertToDTOList(plants);
        
        PlantResponseDTO response = new PlantResponseDTO(
            "success",
            "Plantas encontradas com sucesso",
            plantDTOs
        );
        
        return ResponseEntity.ok(response);
    }
    
    // Endpoint para buscar planta por ID (acessível a todos)
    @GetMapping("/{id}")
    public ResponseEntity<PlantResponseDTO> getPlantById(@PathVariable UUID id) {
        Plant plant = plantService.getPlantById(id);
        
        if (plant != null) {
            List<PlantDTO> plantDTOs = new ArrayList<>();
            plantDTOs.add(convertToDTO(plant));
            
            PlantResponseDTO response = new PlantResponseDTO(
                "success",
                "Planta encontrada com sucesso",
                plantDTOs
            );
            
            return ResponseEntity.ok(response);
        } else {
            PlantResponseDTO response = new PlantResponseDTO(
                "error",
                "Planta não encontrada",
                null
            );
            
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }
    
    // Endpoint para buscar plantas por nome popular (acessível a todos)
    @GetMapping("/search/popular")
    public ResponseEntity<PlantResponseDTO> searchByPopularName(@RequestParam String name) {
        List<Plant> plants = plantService.getPlantsByPopularName(name);
        List<PlantDTO> plantDTOs = convertToDTOList(plants);
        
        PlantResponseDTO response = new PlantResponseDTO(
            "success",
            plants.isEmpty() ? "Nenhuma planta encontrada" : "Plantas encontradas com sucesso",
            plantDTOs
        );
        
        return ResponseEntity.ok(response);
    }
    
    // Endpoint para buscar plantas por nome científico (acessível a todos)
    @GetMapping("/search/scientific")
    public ResponseEntity<PlantResponseDTO> searchByScientificName(@RequestParam String name) {
        List<Plant> plants = plantService.getPlantsByScientificName(name);
        List<PlantDTO> plantDTOs = convertToDTOList(plants);
        
        PlantResponseDTO response = new PlantResponseDTO(
            "success",
            plants.isEmpty() ? "Nenhuma planta encontrada" : "Plantas encontradas com sucesso",
            plantDTOs
        );
        
        return ResponseEntity.ok(response);
    }
    
    // Endpoint para buscar plantas por termo (nome popular ou científico) (acessível a todos)
    @GetMapping("/search")
    public ResponseEntity<PlantResponseDTO> searchPlants(@RequestParam String term) {
        List<Plant> plants = plantService.searchPlants(term);
        List<PlantDTO> plantDTOs = convertToDTOList(plants);
        
        PlantResponseDTO response = new PlantResponseDTO(
            "success",
            plants.isEmpty() ? "Nenhuma planta encontrada" : "Plantas encontradas com sucesso",
            plantDTOs
        );
        
        return ResponseEntity.ok(response);
    }
}