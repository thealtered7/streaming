package com.keene.streaming.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

import com.keene.streaming.core.models.GeoClient;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface GeoClientRepository extends JpaRepository<GeoClient, Long>, GeoClientRepositoryCustom {
    
    /**
     * Find a GeoClient by its GUID.
     * Automatically implemented by Spring Data JPA query derivation.
     * 
     * @param guid the GUID to search for
     * @return Optional containing the GeoClient if found, empty otherwise
     */
    Optional<GeoClient> findByGuid(@NonNull UUID guid);
    
    /**
     * Check if a GeoClient exists with the given GUID.
     * Automatically implemented by Spring Data JPA query derivation.
     * 
     * @param guid the GUID to check
     * @return true if a GeoClient exists with the given GUID, false otherwise
     */
    boolean existsByGuid(@NonNull UUID guid);
}

