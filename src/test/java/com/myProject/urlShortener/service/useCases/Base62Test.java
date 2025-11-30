package com.myProject.urlShortener.service.useCases;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

class Base62Test {
    private final Base62 base62 = new Base62();

    @Test
    @DisplayName("Should Convert ID 0 to '0'")
    void shouldEncodeZero() {
        long input = 0L;
        String expected = "0";

        String result = base62.encode(input);

        Assertions.assertEquals(expected, result);
    }

    @Test
    @DisplayName("Should encode right a simple (10 -> A)")
    void shouldEncodeSimpleId() {
        long input = 10L;
        String expected = "A";

        String result = base62.encode(input);

        Assertions.assertEquals(expected, result);
    }

    @Test
    @DisplayName("Should work round trip")
    void shouldDecodeWhatWasEncoded() {
        long originalId = 123456789L;

        String encoded = base62.encode(originalId);
        long decoded = base62.decode(encoded);

        Assertions.assertEquals(originalId, decoded);
    }

    @Test
    @DisplayName("Should throw exception if invalid character")
    void shouldThrowExceptionForInvalidCharacter() {
        String invalidInput = "A-B";

        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            base62.decode(invalidInput);
        });
    }
}