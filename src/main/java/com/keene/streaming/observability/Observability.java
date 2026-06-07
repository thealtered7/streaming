package com.keene.streaming.observability;

import java.util.Map;
import java.util.function.Supplier;

import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.observation.Observation;
import io.micrometer.observation.ObservationRegistry;

@Component
public class Observability {

    public static final String PREFIX = "geo_service";

    private final ObservationRegistry observationRegistry;
    private final MeterRegistry meterRegistry;

    public Observability(ObservationRegistry observationRegistry, MeterRegistry meterRegistry) {
        this.observationRegistry = observationRegistry;
        this.meterRegistry = meterRegistry;
    }

    public static String spanName(String operation) {
        return PREFIX + "." + operation;
    }

    public static String metricName(String operation) {
        return PREFIX + "." + operation + ".requests";
    }

    public <T> T observeHttp(String operation, Map<String, String> tags, Supplier<T> action) {
        String status = "200";
        Observation observation = Observation.createNotStarted(spanName(operation), observationRegistry);
        if (tags != null) {
            tags.forEach(observation::lowCardinalityKeyValue);
        }
        observation.start();
        try (Observation.Scope scope = observation.openScope()) {
            return action.get();
        } catch (ResponseStatusException ex) {
            status = String.valueOf(ex.getStatusCode().value());
            observation.error(ex);
            throw ex;
        } catch (RuntimeException ex) {
            status = "500";
            observation.error(ex);
            throw ex;
        } finally {
            observation.lowCardinalityKeyValue("http.status_code", status);
            meterRegistry.counter(metricName(operation), "status", status).increment();
            observation.stop();
        }
    }

    public void observeHttpVoid(String operation, Map<String, String> tags, Runnable action) {
        observeHttp(operation, tags, () -> {
            action.run();
            return null;
        });
    }

    public <T> T observeService(String operation, Supplier<T> action) {
        Observation observation = Observation.createNotStarted(spanName(operation), observationRegistry);
        observation.start();
        try (Observation.Scope scope = observation.openScope()) {
            return action.get();
        } catch (RuntimeException ex) {
            observation.error(ex);
            throw ex;
        } finally {
            observation.stop();
        }
    }

    public void observeServiceVoid(String operation, Runnable action) {
        observeService(operation, () -> {
            action.run();
            return null;
        });
    }
}
