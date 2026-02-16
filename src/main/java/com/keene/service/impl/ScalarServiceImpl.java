package com.keene.service.impl;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

import com.keene.service.ScalarService;
import com.keene.streaming.core.models.Scalar;
import com.keene.streaming.repository.ScalarRepository;

@Service
public class ScalarServiceImpl implements ScalarService {
    private static final Logger logger = LoggerFactory.getLogger(ScalarServiceImpl.class);

    private final ScalarRepository scalarRepository;

    @Autowired
    public ScalarServiceImpl(ScalarRepository scalarRepository) {
        this.scalarRepository = scalarRepository;
    }

    @Override
    public Scalar createScalar(@NonNull Scalar scalar) {
        logger.info("Creating scalar: {}", scalar);
        return scalarRepository.save(scalar);
    }

    @Override
    public Scalar getScalar(@NonNull Long id) {
        logger.info("Getting scalar by id: {}", id);
        return scalarRepository.findById(id).orElse(null);
    }

    @Override
    public Scalar updateScalar(@NonNull Long id, @NonNull Scalar scalar) {
        if (id <= 0) {
            throw new IllegalArgumentException("Scalar id must be greater than 0");
        }
        scalar.setId(id);
        logger.info("Updating scalar: {}", scalar);
        return scalarRepository.save(scalar);
    }

    @Override
    public void deleteScalar(@NonNull Long id) {
        logger.info("Deleting scalar by id: {}", id);
        scalarRepository.deleteById(id);
    }

    @Override
    public List<Scalar> getAllScalars() {
        return scalarRepository.findAll();
    }

    @Override
    public Scalar getScalarByName(@NonNull String name) {
        logger.info("Getting scalar by name: {}", name);
        return scalarRepository.findByName(name).orElse(null);
    }

    @Override
    public boolean existsScalarByName(@NonNull String name) {
        logger.info("Checking if scalar exists by name: {}", name);
        return scalarRepository.existsByName(name);
    }

    @Override
    public long countScalars() {
        logger.info("Counting scalars");
        return scalarRepository.countScalars();
    }

    @Override
    public boolean existsScalarById(@NonNull Long id) {
        logger.info("Checking if scalar exists by id: {}", id);
        return scalarRepository.existsById(id);
    }
}
