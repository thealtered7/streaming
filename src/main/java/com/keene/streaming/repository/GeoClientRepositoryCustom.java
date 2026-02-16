package com.keene.streaming.repository;

import org.springframework.lang.NonNull;

/**
 * Custom repository interface for GeoClientRepository.
 * Methods defined here will be implemented in GeoClientRepositoryImpl.
 */
public interface GeoClientRepositoryCustom {
    
    /**
     * Count all geo clients.
     * Custom implementation for countGeoClients() method.
     * 
     * @return the count of all geo clients
     */
    @NonNull
    Long countGeoClients();
}

