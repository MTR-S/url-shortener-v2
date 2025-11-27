package com.myProject.urlShortener.data.repository;

import com.myProject.urlShortener.data.entity.UrlEntity;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.*;

import java.util.Map;
import java.util.Optional;

@Repository
@Primary
public class UrlRepositoryDynamoDb implements UrlRepository {

    private final DynamoDbClient dynamoDbClient;
    private final DynamoDbEnhancedClient enhancedClient;
    private final DynamoDbTable<UrlEntity> urlTable;


    public UrlRepositoryDynamoDb(DynamoDbClient dynamoDbClient, DynamoDbEnhancedClient enhancedClient) {
        this.dynamoDbClient = dynamoDbClient;
        this.enhancedClient = enhancedClient;
        this.urlTable = enhancedClient.table(System.getenv("TABLE_NAME"), TableSchema.fromBean(UrlEntity.class));
    }

    @Override
    public void save(String shortCode, String originalUrl, Long expirationTime) {
        UrlEntity entity = new UrlEntity(shortCode, originalUrl, expirationTime);

        urlTable.putItem(entity);
    }

    @Override
    public Optional<String> findByShortCode(String shortCode) {
        UrlEntity entity = urlTable.getItem(
                requestBuilder -> requestBuilder.key(
                keyBuilder -> keyBuilder.partitionValue(shortCode)
                ));

        if (entity == null) {
            return Optional.empty();
        }

        return Optional.of(entity.getOriginalUrl());
    }

    @Override
    public long incrementAndGetSequenceId() {
        UpdateItemRequest request = buildSequenceIdRequest();
        UpdateItemResponse response = dynamoDbClient.updateItem(request);

        String newValueStr = response.attributes().get("current_value").n();

        return Long.parseLong(newValueStr);
    }

    private UpdateItemRequest buildSequenceIdRequest() {
        return UpdateItemRequest.builder()
                .tableName("UrlShortenerV2")
                .key(Map.of("shortCode", AttributeValue.builder().s("___SEQUENCE___").build()))
                .updateExpression("ADD current_value :inc")
                .expressionAttributeValues(Map.of(":inc", AttributeValue.builder().n("1").build()))
                .returnValues(ReturnValue.UPDATED_NEW)
                .build();
    }

    @Override
    public Optional<UrlEntity> incrementAndGetOriginalUrl(String shortCode) {
        try {
            UpdateItemRequest request = buildIncrementClickCountRequest(shortCode);

            UpdateItemResponse response = dynamoDbClient.updateItem(request);

            return setOptionalUrlEntity(shortCode, response);

        } catch (ConditionalCheckFailedException e) {
            return Optional.empty();
        }
    }

    private UpdateItemRequest buildIncrementClickCountRequest(String shortCode) {
        return UpdateItemRequest.builder()
                .tableName(System.getenv("TABLE_NAME"))
                .key(Map.of("shortCode", AttributeValue.builder().s(shortCode).build()))

                .updateExpression("ADD clickCount :inc")
                .expressionAttributeValues(Map.of(":inc", AttributeValue.builder().n("1").build()))

                .conditionExpression("attribute_exists(shortCode)")

                .returnValues(ReturnValue.ALL_NEW)
                .build();
    }

    private Optional<UrlEntity> setOptionalUrlEntity(String shortCode, UpdateItemResponse response) {
        Map<String, AttributeValue> attributes = response.attributes();

        String originalUrl = attributes.get("originalUrl").s();
        long expirationTime = Long.parseLong(attributes.get("expirationTime").n());

        return Optional.of(new UrlEntity(shortCode, originalUrl, expirationTime));
    }
}