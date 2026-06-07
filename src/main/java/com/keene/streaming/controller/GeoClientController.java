package com.keene.streaming.controller;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.lang.NonNull;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.keene.service.GeoService;
import com.keene.streaming.core.models.GeoClient;
import com.keene.streaming.observability.Observability;

@RestController
public class GeoClientController {

    private static final Logger logger = LoggerFactory.getLogger(GeoClientController.class);

    private final GeoService geoService;
    private final Observability observability;

    public GeoClientController(GeoService geoService, Observability observability) {
        this.geoService = geoService;
        this.observability = observability;
    }

    @GetMapping("/health")
    public String health() {
        return observability.observeHttp("health", Map.of(), () -> {
            logger.info("Health check requested");
            return "OK";
        });
    }

    @GetMapping("/geo-clients")
    public List<GeoClient> getGeoClients() {
        return observability.observeHttp("geo_client.list", Map.of(), () -> {
            logger.info("Getting all geo clients");
            return geoService.getAllGeoClients();
        });
    }

    @PostMapping("/geo-clients")
    public GeoClient createGeoClient(@RequestBody GeoClient geoClient) {
        return observability.observeHttp("geo_client.create", Map.of(), () -> {
            if (geoClient == null) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "GeoClient parameter cannot be null");
            }
            logger.info("Creating geo client: {}", geoClient);
            return geoService.createGeoClient(geoClient);
        });
    }

    @GetMapping("/geo-clients/{id}")
    public GeoClient getGeoClient(@NonNull @PathVariable Long id) {
        return observability.observeHttp("geo_client.get", Map.of("geo_client.id", id.toString()), () -> {
            logger.info("Getting geo client by id: {}", id);
            GeoClient geoClient = geoService.getGeoClient(id);
            if (geoClient == null) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "GeoClient not found");
            }
            return geoClient;
        });
    }

    @PutMapping("/geo-clients/{id}")
    public GeoClient updateGeoClient(@NonNull @PathVariable Long id, @RequestBody GeoClient geoClient) {
        return observability.observeHttp("geo_client.put", Map.of("geo_client.id", id.toString()), () -> {
            if (geoClient == null) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "GeoClient parameter cannot be null");
            }
            logger.info("Updating geo client by id: {}", id);
            return geoService.updateGeoClient(geoClient);
        });
    }
}
