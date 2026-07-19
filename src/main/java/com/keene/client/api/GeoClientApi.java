package com.keene.client.api;

import java.io.IOException;
import java.util.UUID;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.keene.client.http.StreamingHttpClient;
import com.keene.streaming.core.models.GeoClient;

public class GeoClientApi {

    private final StreamingHttpClient httpClient;
    private final ObjectMapper objectMapper;

    public GeoClientApi(StreamingHttpClient httpClient, ObjectMapper objectMapper) {
        this.httpClient = httpClient;
        this.objectMapper = objectMapper;
    }

    public GeoClient createGeoClient(UUID guid) throws IOException, InterruptedException {
        GeoClient request = new GeoClient(guid);
        String responseBody = httpClient.post("/geo-clients", objectMapper.writeValueAsString(request));
        return objectMapper.readValue(responseBody, GeoClient.class);
    }
}
