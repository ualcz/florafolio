package com.florafolio.controller;

import com.florafolio.dto.auth.ResponseDTO;
import com.florafolio.dto.plant.CreatePlantRequestDTO;
import com.florafolio.dto.plant.PlantDTO;
import com.florafolio.dto.plant.PlantResponseDTO;
import com.florafolio.dto.plant.UpdatePlantRequestDTO;
import com.florafolio.model.Plant;
import com.florafolio.service.PlantService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/plants")
public class PlantController {
    
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
    
    // Converter CreatePlantRequestDTO para Plant
    private Plant convertToEntity(CreatePlantRequestDTO dto) {
        return new Plant(
            null,
            dto.getPopularName(),
            dto.getScientificName(),
            dto.getDescription(),
            dto.getFamily(),
            dto.getOrigin(),
            dto.getCareInstructions(),
            dto.getImageUrl()
        );
    }
    
    // Converter UpdatePlantRequestDTO para Plant
    private Plant convertToEntity(UpdatePlantRequestDTO dto) {
        return new Plant(
            null,
            dto.getPopularName(),
            dto.getScientificName(),
            dto.getDescription(),
            dto.getFamily(),
            dto.getOrigin(),
            dto.getCareInstructions(),
            dto.getImageUrl()
        );
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
    
    // Endpoint para criar nova planta (apenas admin)
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ResponseDTO> createPlant(@Valid @RequestBody CreatePlantRequestDTO createPlantRequest) {
        Plant plant = convertToEntity(createPlantRequest);
        Plant savedPlant = plantService.createPlant(plant);
        
        ResponseDTO response = new ResponseDTO(
            "success",
            "Planta criada com sucesso"
        );
        
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    
    // Endpoint para atualizar planta existente (apenas admin)
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ResponseDTO> updatePlant(
            @PathVariable UUID id,
            @Valid @RequestBody UpdatePlantRequestDTO updatePlantRequest) {
        
        Plant plantDetails = convertToEntity(updatePlantRequest);
        Plant updatedPlant = plantService.updatePlant(id, plantDetails);
        
        if (updatedPlant != null) {
            ResponseDTO response = new ResponseDTO(
                "success",
                "Planta atualizada com sucesso"
            );
            
            return ResponseEntity.ok(response);
        } else {
            ResponseDTO response = new ResponseDTO(
                "error",
                "Planta não encontrada"
            );
            
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }
    
    // Endpoint para excluir planta (apenas admin)
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ResponseDTO> deletePlant(@PathVariable UUID id) {
        boolean deleted = plantService.deletePlant(id);
        
        if (deleted) {
            ResponseDTO response = new ResponseDTO(
                "success",
                "Planta excluída com sucesso"
            );
            
            return ResponseEntity.ok(response);
        } else {
            ResponseDTO response = new ResponseDTO(
                "error",
                "Planta não encontrada"
            );
            
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }
}