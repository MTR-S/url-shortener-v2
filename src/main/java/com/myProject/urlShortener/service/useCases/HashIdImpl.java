package com.myProject.urlShortener.service.useCases;

import com.myProject.urlShortener.service.interfaces.Decoder;
import com.myProject.urlShortener.service.interfaces.Encoder;
import org.hashids.Hashids;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

@Component
@Primary
public class HashIdImpl implements Encoder, Decoder {
    private final Hashids hashids;

    private static final String ALPHABET = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final int MIN_LENGTH = 4;

    public HashIdImpl(@Value("${HASH_SALT:default_dev_salt}") String salt) {
        this.hashids = new Hashids(salt, MIN_LENGTH, ALPHABET);
    }

    @Override
    public String encode(long databaseId) {
        return hashids.encode(databaseId);
    }

    @Override
    public long decode(String shortCode) {
        long[] decodedNumbers = hashids.decode(shortCode);

        if (decodedNumbers.length == 0) {
            throw new IllegalArgumentException("Invalid code or cannot be decoded.");
        }

        return decodedNumbers[0];
    }
}
