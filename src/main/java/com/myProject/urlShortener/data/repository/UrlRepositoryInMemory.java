package com.myProject.urlShortener.data.repository;

import com.myProject.urlShortener.data.entity.UrlEntity;
import com.myProject.urlShortener.data.repository.interfaces.UrlRepository;

import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Component
public class UrlRepositoryInMemory implements UrlRepository {

    private final AtomicLong idGenerator = new AtomicLong(15_000_000);

    private final Map<String, String> storage = new ConcurrentHashMap<>();

    @Override
    public void save(String shortCode, String originalUrl, Long expirationTime) {
        storage.put(shortCode, originalUrl);
    }

    @Override
    public Optional<String> findByShortCode(String shortCode) {
        return Optional.ofNullable(storage.get(shortCode));
    }

    @Override
    public long incrementAndGetSequenceId() {
        return idGenerator.incrementAndGet();
    }

    @Override
    public Optional<UrlEntity> incrementAndGetOriginalUrl(String shortCode) {
        return null;
    }

    @Override
    public void saveCustomAlias(String shortCode, String originalUrl, long expirationTime) {}
}
