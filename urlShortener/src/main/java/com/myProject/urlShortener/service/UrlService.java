package com.myProject.urlShortener.service;

import org.springframework.stereotype.Service;
import com.myProject.urlShortener.service.interfaces.Encoder;
import com.myProject.urlShortener.service.interfaces.Decoder;

@Service
public class UrlService{

    Encoder encoderInterface;
    Decoder decoderInterface;

    public UrlService(Encoder encoderInterface, Decoder decoderInterface) {
        this.encoderInterface = encoderInterface;
        this.decoderInterface = decoderInterface;
    }

    public String encode(long autoIncrementId) {
        return encoderInterface.encode(autoIncrementId);
    }


    public long decode(String stringToBeDecoded) {
        return decoderInterface.decode(stringToBeDecoded);
    }
}
