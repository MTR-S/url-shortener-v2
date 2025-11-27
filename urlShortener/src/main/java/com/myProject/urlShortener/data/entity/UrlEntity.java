package com.myProject.urlShortener.data.entity;

import lombok.Data;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;

@Data
@DynamoDbBean
public class UrlEntity {
    private String shortCode;
    private String originalUrl;
    private Long expirationTime;
    private Long clickCount;

    public UrlEntity() {}

    public UrlEntity(String shortCode, String originalUrl, Long expirationTime) {
        this.shortCode = shortCode;
        this.originalUrl = originalUrl;
        this.expirationTime = expirationTime;
        this.clickCount = 0L;
    }

    @DynamoDbPartitionKey
    public String getShortCode() {
        return shortCode;
    }
}
