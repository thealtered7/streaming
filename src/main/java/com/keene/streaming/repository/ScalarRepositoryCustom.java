package com.keene.streaming.repository;

import org.springframework.lang.NonNull;

/**
 * Custom repository interface for ScalarRepository.
 * Methods defined here will be implemented in ScalarRepositoryImpl.
 */
public interface ScalarRepositoryCustom {

    /**
     * Count all scalars.
     *
     * @return the count of all scalars
     */
    @NonNull
    Long countScalars();
}
