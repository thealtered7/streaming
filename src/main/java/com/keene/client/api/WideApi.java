package com.keene.client.api;

import java.io.IOException;
import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.keene.client.http.StreamingHttpClient;
import com.keene.streaming.core.models.Wide;
import com.keene.streaming.core.models.WidePage;

public class WideApi {

    private final StreamingHttpClient httpClient;
    private final ObjectMapper objectMapper;

    public WideApi(StreamingHttpClient httpClient, ObjectMapper objectMapper) {
        this.httpClient = httpClient;
        this.objectMapper = objectMapper;
    }

    public List<Wide> listWides() throws IOException, InterruptedException {
        String responseBody = httpClient.get("/wide");
        WidePage page = objectMapper.readValue(responseBody, WidePage.class);
        return page.getWides();
    }

    public List<Wide> listWides(int offset) throws IOException, InterruptedException {
        String responseBody = httpClient.get("/wide?offset=" + offset);
        WidePage page = objectMapper.readValue(responseBody, WidePage.class);
        return page.getWides();
    }

    public void deleteWide(long id) throws IOException, InterruptedException {
        httpClient.delete("/wide/" + id);
    }

    public Wide createWide(Wide request) throws IOException, InterruptedException {
        String responseBody = httpClient.post("/wide", objectMapper.writeValueAsString(request));
        return objectMapper.readValue(responseBody, Wide.class);
    }

    public Wide updateWide(long id, Wide request) throws IOException, InterruptedException {
        String responseBody = httpClient.put("/wide/" + id, objectMapper.writeValueAsString(request));
        return objectMapper.readValue(responseBody, Wide.class);
    }
}
