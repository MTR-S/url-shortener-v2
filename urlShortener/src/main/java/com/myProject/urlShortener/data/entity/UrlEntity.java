package com.myProject.urlShortener.data.entity;

import lombok.Getter;
import lombok.Setter;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;

@Getter
@Setter
@DynamoDbBean
public class UrlEntity {
    private String shortCode;
    private String originalUrl;

    public UrlEntity() {}

    public UrlEntity(String shortCode, String originalUrl) {
        this.shortCode = shortCode;
        this.originalUrl = originalUrl;
    }

    @DynamoDbPartitionKey
    public String getShortCode() {
        return shortCode;
    }
}
