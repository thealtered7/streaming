package com.keene.service;

import org.springframework.lang.NonNull;

import com.keene.streaming.core.models.Wide;
import com.keene.streaming.core.models.WidePage;

public interface WideService {
    Wide createWide(@NonNull Wide wide);
    Wide getWide(@NonNull Long id);
    Wide updateWide(@NonNull Long id, @NonNull Wide wide);
    void deleteWide(@NonNull Long id);
    WidePage getAllWides(int offset, int count);
    long countWides();
    boolean existsWideById(@NonNull Long id);
}
