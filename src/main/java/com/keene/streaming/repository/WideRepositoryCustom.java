package com.keene.streaming.repository;

import java.util.List;

import org.springframework.lang.NonNull;

import com.keene.streaming.core.models.Wide;

/**
 * Custom repository interface for WideRepository.
 * Methods defined here will be implemented in WideRepositoryImpl.
 */
public interface WideRepositoryCustom {

    /**
     * Count all wide rows.
     *
     * @return the count of all wide rows
     */
    @NonNull
    Long countWides();

    /**
     * Find a page of wide rows ordered by id.
     *
     * @param offset the number of rows to skip
     * @param count  the maximum number of rows to return
     * @return the rows in the requested range, empty if the range is beyond existing data
     */
    @NonNull
    List<Wide> findWides(int offset, int count);
}
