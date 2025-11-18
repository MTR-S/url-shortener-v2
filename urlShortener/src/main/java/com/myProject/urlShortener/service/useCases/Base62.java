package com.myProject.urlShortener.service.useCases;

import com.myProject.urlShortener.service.interfaces.Decoder;
import com.myProject.urlShortener.service.interfaces.Encoder;

public class Base62 implements Encoder, Decoder {
    @Override
    public String encode(Integer autoIncrementId) {
        return "";
    }

    @Override
    public String decode(String stringToBeDecoded) {
        return "";
    }
}
