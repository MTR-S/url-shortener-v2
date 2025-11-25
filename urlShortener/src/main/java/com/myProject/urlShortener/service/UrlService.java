package com.myProject.urlShortener.service;

import com.myProject.urlShortener.data.repository.UrlRepository;
import org.springframework.stereotype.Service;
import com.myProject.urlShortener.service.interfaces.Encoder;
import com.myProject.urlShortener.service.interfaces.Decoder;

import java.util.concurrent.atomic.AtomicLong;

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

    public String shortMyUrl(String originalUrl) {
         long id = urlRepository.incrementAndGetId();

        String shortCode = encoderInterface.encode(id);

        urlRepository.save(shortCode, originalUrl);

        return shortCode;
    }


    public String getOriginalUrl(String shortCode) {
        return urlRepository.findByShortCode(shortCode)
                .orElseThrow(() -> new RuntimeException("URL not founded"));
    }
}
