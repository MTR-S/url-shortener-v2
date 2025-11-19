package com.myProject.urlShortener.service.useCases;

import com.myProject.urlShortener.service.interfaces.Decoder;
import com.myProject.urlShortener.service.interfaces.Encoder;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Component
public class Base62 implements Encoder, Decoder {
    private static final String BASE62_CHARS = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
    private static final int BASE = 62;
    private static final int[] LOOKUP_TABLE = new int[128];

    static {
        Arrays.fill(LOOKUP_TABLE, -1);

        for (int i = 0; i < BASE62_CHARS.length(); i++) {
            char character = BASE62_CHARS.charAt(i);
            LOOKUP_TABLE[character] = i;
        }
    }

    @Override
    public String encode(long databaseId) {
        if (databaseId == 0) {
            return "0";
        }

        long numerator = databaseId;

        buildBase62String(numerator);

        return buildBase62String(numerator).reverse().toString();
    }

    private StringBuilder buildBase62String(long numerator) {
        StringBuilder encodedString = new StringBuilder(11);

        while (numerator > 0) {
            int remainder = (int) (numerator % BASE);

            encodedString.append(BASE62_CHARS.charAt(remainder));

            numerator /= BASE;
        }

        return encodedString;
    }

    @Override
    public long decode(String shortCode) {
        long result = 0;
        int length = shortCode.length();

        for (int index = 0; index < length; index++) {
            char character = shortCode.charAt(index);

            if (character >= LOOKUP_TABLE.length || LOOKUP_TABLE[character] == -1) {
                throw new IllegalArgumentException("Invalid character: " + character);
            }

            int value = LOOKUP_TABLE[character];

            result = result * BASE + value;
        }

        return result;
    }
}
