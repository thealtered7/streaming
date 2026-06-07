package com.keene.service.impl;

import java.util.List;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

import com.keene.service.GeoService;
import com.keene.streaming.core.models.GeoClient;
import com.keene.streaming.observability.GeoServiceObservability;
import com.keene.streaming.repository.GeoClientRepository;

@Service
public class GeoServiceImpl implements GeoService {
    private static final Logger logger = LoggerFactory.getLogger(GeoServiceImpl.class);

    private final GeoClientRepository geoClientRepository;
    private final GeoServiceObservability observability;

    public GeoServiceImpl(GeoClientRepository geoClientRepository, GeoServiceObservability observability) {
        this.geoClientRepository = geoClientRepository;
        this.observability = observability;
    }

    @Override
    public GeoClient createGeoClient(@NonNull GeoClient geoClient) {
        return observability.observeService("geo_service.create_geo_client", () -> geoClientRepository.save(geoClient));
    }

    @Override
    public GeoClient getGeoClient(@NonNull Long id) {
        return observability.observeService("geo_service.get_geo_client", () -> {
            logger.info("Getting geo client by id: {}", id);
            return geoClientRepository.findById(id).orElse(null);
        });
    }

    @Override
    public GeoClient updateGeoClient(@NonNull GeoClient geoClient) {
        return observability.observeService("geo_service.update_geo_client", () -> {
            if (geoClient.getId() == null) {
                throw new IllegalArgumentException("Geo client id cannot be null");
            } else if (geoClient.getId() <= 0) {
                throw new IllegalArgumentException("Geo client id must be greater than 0");
            }
            logger.info("Updating geo client: {}", geoClient);
            return geoClientRepository.save(geoClient);
        });
    }

    @Override
    public void deleteGeoClient(@NonNull Long id) {
        observability.observeServiceVoid("geo_service.delete_geo_client", () -> {
            logger.info("Deleting geo client by id: {}", id);
            geoClientRepository.deleteById(id);
        });
    }

    @Override
    public List<GeoClient> getAllGeoClients() {
        return observability.observeService("geo_service.get_all_geo_clients", () -> geoClientRepository.findAll());
    }

    @Override
    public GeoClient getGeoClientByGuid(@NonNull UUID guid) {
        return observability.observeService("geo_service.get_geo_client_by_guid", () -> {
            logger.info("Getting geo client by guid: {}", guid);
            return geoClientRepository.findByGuid(guid).orElse(null);
        });
    }

    @Override
    public boolean existsGeoClientByGuid(@NonNull UUID guid) {
        return observability.observeService("geo_service.exists_geo_client_by_guid", () -> {
            logger.info("Checking if geo client exists by guid: {}", guid);
            return geoClientRepository.existsByGuid(guid);
        });
    }

    @Override
    public boolean existsGeoClientById(@NonNull Long id) {
        return observability.observeService("geo_service.exists_geo_client_by_id", () -> {
            logger.info("Checking if geo client exists by id: {}", id);
            return geoClientRepository.existsById(id);
        });
    }

    @Override
    public long countGeoClients() {
        return observability.observeService("geo_service.count_geo_clients", () -> {
            logger.info("Counting geo clients");
            return geoClientRepository.countGeoClients();
        });
    }
}
