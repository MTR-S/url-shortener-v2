package com.myProject.urlShortener.data.repository.interfaces;

import com.myProject.urlShortener.data.entity.UrlEntity;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UrlRepository {
    void save(String shortCode, String originalUrl, Long expirationTime);
    Optional<String> findByShortCode(String shortCode);
    long incrementAndGetSequenceId();
    Optional<UrlEntity> incrementAndGetOriginalUrl(String shortCode);
    void saveCustomAlias(String shortCode, String originalUrl, long expirationTime);
}
