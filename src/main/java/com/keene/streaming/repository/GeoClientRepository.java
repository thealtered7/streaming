package com.keene.streaming.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.keene.streaming.core.models.GeoClient;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface GeoClientRepository extends JpaRepository<GeoClient, Long> {
    
    Optional<GeoClient> findByGuid(UUID guid);
    
    boolean existsByGuid(UUID guid);
}

