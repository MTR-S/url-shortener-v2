package com.myProject.urlShortener.data.repository;

import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UrlRepository {
    void save(String shortCode, String originalUrl);
    Optional<String> findByShortCode(String shortCode);
    long incrementAndGetId();
}
