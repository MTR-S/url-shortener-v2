package com.myProject.urlShortener.controller;

import com.myProject.urlShortener.data.dto.ShortenUrlRequest;
import com.myProject.urlShortener.data.dto.ShortenUrlResponse;
import com.myProject.urlShortener.service.UrlService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
public class MainController {

    private final UrlService urlService;

    public MainController(UrlService urlService) {
        this.urlService = urlService;
    }

    @PostMapping("/shorten")
    public ResponseEntity<ShortenUrlResponse> shorten(@RequestBody ShortenUrlRequest request, HttpServletRequest servletRequest) {
        Optional<Long> expirateAt = Optional.of(request.expirationTime());

        String shortCode = urlService.shortMyUrl(request.originalUrl(), expirateAt.get());

        String redirectUrl = servletRequest.getRequestURL().toString().replace("/shorten", "/" + shortCode);

        ShortenUrlResponse response = new ShortenUrlResponse(redirectUrl, shortCode);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{shortCode}")
    public ResponseEntity<Void> redirect(@PathVariable String shortCode) {
        String originalUrl = urlService.getOriginalUrl(shortCode);

        return ResponseEntity.status(HttpStatus.FOUND)
                .header(HttpHeaders.LOCATION, originalUrl)
                .build();
    }
}
