package com.myProject.urlShortener.service;

import com.myProject.urlShortener.data.entity.UrlEntity;
import com.myProject.urlShortener.data.repository.interfaces.UrlRepository;
import com.myProject.urlShortener.service.interfaces.Decoder;
import com.myProject.urlShortener.service.interfaces.Encoder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Service
public class UrlService {

    private static final int GET_ID_MAX_ATTEMPTS = 10;
    private static final long DEFAULT_EXPIRATION_DAYS = 3650;

    private final Encoder encoderInterface;
    private final Decoder decoderInterface;
    private final UrlRepository urlRepository;

    public UrlService(Encoder encoderInterface, Decoder decoderInterface, UrlRepository urlRepository) {
        this.encoderInterface = encoderInterface;
        this.decoderInterface = decoderInterface;
        this.urlRepository = urlRepository;
    }

    public String shortMyUrl(String originalUrl, Long customExpirationTime, String customAlias) {
        long expirationTime = calculateExpirationTime(customExpirationTime);

        if (customAlias != null && !customAlias.isEmpty()) {
            return saveCustomAlias(customAlias, originalUrl, expirationTime);
        }

        int attempts = 0;
        String shortCode = "";

        do {
            attempts++;
            if (attempts > GET_ID_MAX_ATTEMPTS) {
                throw new RuntimeException("Error generating url: The system is congested or without available IDs");
            }

            shortCode = saveUrlWithNoCollision(originalUrl, expirationTime);

        } while (shortCode.isEmpty());

        return shortCode;
    }

    private long calculateExpirationTime(Long customExpirationTime) {
        long currentSeconds = Instant.now().getEpochSecond();
        long defaultExpirationTime = Instant.now().plus(DEFAULT_EXPIRATION_DAYS, ChronoUnit.DAYS).getEpochSecond();

        if (customExpirationTime == null || customExpirationTime < currentSeconds) {
            return defaultExpirationTime;
        }

        return customExpirationTime;
    }

    private String saveCustomAlias(String customAlias, String originalUrl, Long expirationTime) {
        if (!customAlias.matches("[a-zA-Z0-9-_]{1,20}")) {
            throw new IllegalArgumentException("Alias invalid. Use only letters, numbers, hyphens and underscores.");
        }

        urlRepository.saveCustomAlias(customAlias, originalUrl, expirationTime);

        return customAlias;
    }

    private String saveUrlWithNoCollision(String originalUrl, Long expirationTime) {
        long id = urlRepository.incrementAndGetSequenceId();
        String shortCode = encoderInterface.encode(id);

        try {
            urlRepository.save(shortCode, originalUrl, expirationTime);
        } catch (RuntimeException e) {
            System.out.println("Collision detected for the code: " + shortCode + ". Trying again...");

            return "";
        }

        return shortCode;
    }

    public String getOriginalUrl(String shortCode) {
        UrlEntity entity = urlRepository.incrementAndGetOriginalUrl(shortCode)
                .orElseThrow(() -> new RuntimeException("URL not found"));

        return entity.getOriginalUrl();
    }
}
