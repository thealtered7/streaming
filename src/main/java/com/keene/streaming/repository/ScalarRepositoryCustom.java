package com.keene.streaming.repository;

import java.util.List;

import org.springframework.lang.NonNull;

import com.keene.streaming.core.models.Scalar;

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

    /**
     * Find a page of scalars ordered by id.
     *
     * @param offset the number of scalars to skip
     * @param count  the maximum number of scalars to return
     * @return the scalars in the requested range, empty if the range is beyond existing data
     */
    @NonNull
    List<Scalar> findScalars(int offset, int count);
}
