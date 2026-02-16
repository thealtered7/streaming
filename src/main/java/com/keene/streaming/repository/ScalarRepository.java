package com.keene.streaming.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

import com.keene.streaming.core.models.Scalar;

@Repository
public interface ScalarRepository extends JpaRepository<Scalar, Long>, ScalarRepositoryCustom {

    /**
     * Find a Scalar by its name.
     *
     * @param name the name to search for
     * @return Optional containing the Scalar if found, empty otherwise
     */
    Optional<Scalar> findByName(@NonNull String name);

    /**
     * Check if a Scalar exists with the given name.
     *
     * @param name the name to check
     * @return true if a Scalar exists with the given name, false otherwise
     */
    boolean existsByName(@NonNull String name);
}
