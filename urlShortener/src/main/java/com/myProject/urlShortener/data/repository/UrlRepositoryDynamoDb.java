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
        this.urlTable = enhancedClient.table("UrlShortenerV2", TableSchema.fromBean(UrlEntity.class));
    }

    @Override
    public void save(String shortCode, String originalUrl) {
        UrlEntity entity = new UrlEntity(shortCode, originalUrl);
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
    public long incrementAndGetId() {
        UpdateItemRequest request = buildRequest();
        UpdateItemResponse response = dynamoDbClient.updateItem(request);

        String newValueStr = response.attributes().get("current_value").n();

        return Long.parseLong(newValueStr);
    }

    public UpdateItemRequest buildRequest() {
        return UpdateItemRequest.builder()
                .tableName("UrlShortenerV2")
                .key(Map.of("shortCode", AttributeValue.builder().s("___SEQUENCE___").build()))
                .updateExpression("ADD current_value :inc")
                .expressionAttributeValues(Map.of(":inc", AttributeValue.builder().n("1").build()))
                .returnValues(ReturnValue.UPDATED_NEW)
                .build();
    }
}