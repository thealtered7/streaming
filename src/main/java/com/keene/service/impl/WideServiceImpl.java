package com.keene.service.impl;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

import com.keene.service.WideService;
import com.keene.streaming.core.models.Wide;
import com.keene.streaming.core.models.WidePage;
import com.keene.streaming.observability.Observability;
import com.keene.streaming.repository.WideRepository;

@Service
public class WideServiceImpl implements WideService {
    private static final Logger logger = LoggerFactory.getLogger(WideServiceImpl.class);

    private final WideRepository wideRepository;
    private final Observability observability;

    public WideServiceImpl(WideRepository wideRepository, Observability observability) {
        this.wideRepository = wideRepository;
        this.observability = observability;
    }

    @Override
    public Wide createWide(@NonNull Wide wide) {
        return observability.observeService("wide_service.create_wide", () -> {
            logger.info("Creating wide: {}", wide.getId());
            return wideRepository.save(wide);
        });
    }

    @Override
    public Wide getWide(@NonNull Long id) {
        return observability.observeService("wide_service.get_wide", () -> {
            logger.info("Getting wide by id: {}", id);
            return wideRepository.findById(id).orElse(null);
        });
    }

    @Override
    public Wide updateWide(@NonNull Long id, @NonNull Wide wide) {
        return observability.observeService("wide_service.update_wide", () -> {
            if (id <= 0) {
                throw new IllegalArgumentException("Wide id must be greater than 0");
            }
            wide.setId(id);
            logger.info("Updating wide: {}", id);
            return wideRepository.save(wide);
        });
    }

    @Override
    public void deleteWide(@NonNull Long id) {
        observability.observeServiceVoid("wide_service.delete_wide", () -> {
            logger.info("Deleting wide by id: {}", id);
            wideRepository.deleteById(id);
        });
    }

    @Override
    public WidePage getAllWides(int offset, int count) {
        return observability.observeService("wide_service.get_all_wides", () -> {
            logger.info("Getting wide rows with offset {} and count {}", offset, count);
            List<Wide> wides = wideRepository.findWides(offset, count);
            return new WidePage(wides, offset, count);
        });
    }

    @Override
    public long countWides() {
        return observability.observeService("wide_service.count_wides", () -> {
            logger.info("Counting wide rows");
            return wideRepository.countWides();
        });
    }

    @Override
    public boolean existsWideById(@NonNull Long id) {
        return observability.observeService("wide_service.exists_wide_by_id", () -> {
            logger.info("Checking if wide exists by id: {}", id);
            return wideRepository.existsById(id);
        });
    }
}
