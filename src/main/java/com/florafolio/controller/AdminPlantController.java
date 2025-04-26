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
@RequestMapping("/admin/plants")
@PreAuthorize("hasAuthority('ADMIN')")
public class AdminPlantController {
    
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
    
    // Endpoint para criar nova planta (apenas admin)
    @PostMapping
    public ResponseEntity<PlantResponseDTO> createPlant(@Valid @RequestBody CreatePlantRequestDTO createPlantRequest) {
        Plant plant = convertToEntity(createPlantRequest);
        Plant savedPlant = plantService.createPlant(plant);
        
        List<PlantDTO> plantDTOs = new ArrayList<>();
        plantDTOs.add(convertToDTO(savedPlant));
        
        PlantResponseDTO response = new PlantResponseDTO(
            "success",
            "Planta criada com sucesso",
            plantDTOs
        );
        
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    
    // Endpoint para atualizar planta existente (apenas admin)
    @PutMapping("/{id}")
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