package com.florafolio.repository;

import com.florafolio.model.Plant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface PlantRepository extends JpaRepository<Plant, UUID> {
    // Busca plantas pelo nome popular (case insensitive, contendo o termo)
    List<Plant> findByPopularNameContainingIgnoreCase(String popularName);
    
    // Busca plantas pelo nome científico (case insensitive, contendo o termo)
    List<Plant> findByScientificNameContainingIgnoreCase(String scientificName);
    
    // Busca plantas pelo nome popular ou científico
    List<Plant> findByPopularNameContainingIgnoreCaseOrScientificNameContainingIgnoreCase(
        String popularName, String scientificName);
}