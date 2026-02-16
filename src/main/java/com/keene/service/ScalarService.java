package com.keene.service;

import java.util.List;

import org.springframework.lang.NonNull;

import com.keene.streaming.core.models.Scalar;

public interface ScalarService {
    Scalar createScalar(@NonNull Scalar scalar);
    Scalar getScalar(@NonNull Long id);
    Scalar updateScalar(@NonNull Long id, @NonNull Scalar scalar);
    void deleteScalar(@NonNull Long id);
    List<Scalar> getAllScalars();
    Scalar getScalarByName(@NonNull String name);
    boolean existsScalarByName(@NonNull String name);
    long countScalars();
    boolean existsScalarById(@NonNull Long id);
}
