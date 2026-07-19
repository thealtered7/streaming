package com.keene.client.api;

import java.io.IOException;
import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.keene.client.http.StreamingHttpClient;
import com.keene.streaming.core.models.Scalar;
import com.keene.streaming.core.models.ScalarPage;

public class ScalarApi {

    private final StreamingHttpClient httpClient;
    private final ObjectMapper objectMapper;

    public ScalarApi(StreamingHttpClient httpClient, ObjectMapper objectMapper) {
        this.httpClient = httpClient;
        this.objectMapper = objectMapper;
    }

    public List<Scalar> listScalars() throws IOException, InterruptedException {
        String responseBody = httpClient.get("/scalars");
        ScalarPage page = objectMapper.readValue(responseBody, ScalarPage.class);
        return page.getScalars();
    }

    public List<Scalar> listScalars(int offset) throws IOException, InterruptedException {
        String responseBody = httpClient.get("/scalars?offset=" + offset);
        ScalarPage page = objectMapper.readValue(responseBody, ScalarPage.class);
        return page.getScalars();
    }

    public void deleteScalar(long id) throws IOException, InterruptedException {
        httpClient.delete("/scalars/" + id);
    }

    public Scalar createScalar(String name, double value) throws IOException, InterruptedException {
        Scalar request = new Scalar();
        request.setName(name);
        request.setValue(value);
        String responseBody = httpClient.post("/scalars", objectMapper.writeValueAsString(request));
        return objectMapper.readValue(responseBody, Scalar.class);
    }

    public Scalar updateScalar(long id, String name, double value) throws IOException, InterruptedException {
        Scalar request = new Scalar();
        request.setName(name);
        request.setValue(value);
        String responseBody = httpClient.put("/scalars/" + id, objectMapper.writeValueAsString(request));
        return objectMapper.readValue(responseBody, Scalar.class);
    }
}
