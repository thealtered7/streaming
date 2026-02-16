package com.keene.service;

import java.util.List;
import java.util.UUID;

import org.springframework.lang.NonNull;

import com.keene.streaming.core.models.GeoClient;

public interface GeoService {
    GeoClient createGeoClient(@NonNull GeoClient geoClient);
    GeoClient getGeoClient(@NonNull Long id);
    GeoClient updateGeoClient(@NonNull GeoClient geoClient);
    void deleteGeoClient(@NonNull Long id);
    List<GeoClient> getAllGeoClients();
    GeoClient getGeoClientByGuid(@NonNull UUID guid);
    boolean existsGeoClientByGuid(@NonNull UUID guid);
    long countGeoClients();
    boolean existsGeoClientById(@NonNull Long id);
}
