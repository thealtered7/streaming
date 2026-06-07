package com.keene.service.impl;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

import com.keene.service.ScalarService;
import com.keene.streaming.core.models.Scalar;
import com.keene.streaming.observability.GeoServiceObservability;
import com.keene.streaming.repository.ScalarRepository;

@Service
public class ScalarServiceImpl implements ScalarService {
    private static final Logger logger = LoggerFactory.getLogger(ScalarServiceImpl.class);

    private final ScalarRepository scalarRepository;
    private final GeoServiceObservability observability;

    public ScalarServiceImpl(ScalarRepository scalarRepository, GeoServiceObservability observability) {
        this.scalarRepository = scalarRepository;
        this.observability = observability;
    }

    @Override
    public Scalar createScalar(@NonNull Scalar scalar) {
        return observability.observeService("scalar_service.create_scalar", () -> {
            logger.info("Creating scalar: {}", scalar);
            return scalarRepository.save(scalar);
        });
    }

    @Override
    public Scalar getScalar(@NonNull Long id) {
        return observability.observeService("scalar_service.get_scalar", () -> {
            logger.info("Getting scalar by id: {}", id);
            return scalarRepository.findById(id).orElse(null);
        });
    }

    @Override
    public Scalar updateScalar(@NonNull Long id, @NonNull Scalar scalar) {
        return observability.observeService("scalar_service.update_scalar", () -> {
            if (id <= 0) {
                throw new IllegalArgumentException("Scalar id must be greater than 0");
            }
            scalar.setId(id);
            logger.info("Updating scalar: {}", scalar);
            return scalarRepository.save(scalar);
        });
    }

    @Override
    public void deleteScalar(@NonNull Long id) {
        observability.observeServiceVoid("scalar_service.delete_scalar", () -> {
            logger.info("Deleting scalar by id: {}", id);
            scalarRepository.deleteById(id);
        });
    }

    @Override
    public List<Scalar> getAllScalars() {
        return observability.observeService("scalar_service.get_all_scalars", () -> scalarRepository.findAll());
    }

    @Override
    public Scalar getScalarByName(@NonNull String name) {
        return observability.observeService("scalar_service.get_scalar_by_name", () -> {
            logger.info("Getting scalar by name: {}", name);
            return scalarRepository.findByName(name).orElse(null);
        });
    }

    @Override
    public boolean existsScalarByName(@NonNull String name) {
        return observability.observeService("scalar_service.exists_scalar_by_name", () -> {
            logger.info("Checking if scalar exists by name: {}", name);
            return scalarRepository.existsByName(name);
        });
    }

    @Override
    public long countScalars() {
        return observability.observeService("scalar_service.count_scalars", () -> {
            logger.info("Counting scalars");
            return scalarRepository.countScalars();
        });
    }

    @Override
    public boolean existsScalarById(@NonNull Long id) {
        return observability.observeService("scalar_service.exists_scalar_by_id", () -> {
            logger.info("Checking if scalar exists by id: {}", id);
            return scalarRepository.existsById(id);
        });
    }
}
