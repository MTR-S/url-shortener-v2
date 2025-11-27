package com.myProject.urlShortener.service;

import com.myProject.urlShortener.data.entity.UrlEntity;
import com.myProject.urlShortener.data.repository.UrlRepository;
import org.springframework.stereotype.Service;
import com.myProject.urlShortener.service.interfaces.Encoder;
import com.myProject.urlShortener.service.interfaces.Decoder;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Service
public class UrlService{

    Encoder encoderInterface;
    Decoder decoderInterface;

    UrlRepository urlRepository;

    public UrlService(Encoder encoderInterface, Decoder decoderInterface,
                      UrlRepository urlRepository) {
        this.encoderInterface = encoderInterface;
        this.decoderInterface = decoderInterface;
        this.urlRepository = urlRepository;
    }

    public String shortMyUrl(String originalUrl, Long customExpirationTime) {
         long id = urlRepository.incrementAndGetSequenceId();

        String shortCode = encoderInterface.encode(id);

        long expirationTime = setExpirationTime(customExpirationTime);


        urlRepository.save(shortCode, originalUrl, expirationTime);

        return shortCode;
    }

    private long setExpirationTime(Long customExpirationTime) {
        long expirationTime;

        if (customExpirationTime == null) {
            long defaultExparationTime = Instant.now().plus(3650, ChronoUnit.DAYS).getEpochSecond();

            expirationTime = defaultExparationTime;
        } else {
            long currentSeconds = Instant.now().getEpochSecond();

            if (customExpirationTime < currentSeconds) {
                long defaultExparationTime = Instant.now().plus(3650, ChronoUnit.DAYS).getEpochSecond();

                customExpirationTime = defaultExparationTime;
            }

            expirationTime = customExpirationTime;
        }

        return expirationTime;
    }

    public String getOriginalUrl(String shortCode) {
        UrlEntity entity = urlRepository.incrementAndGetOriginalUrl(shortCode)
                .orElseThrow(() -> new RuntimeException("URL not founded"));

        return entity.getOriginalUrl();
    }
}
