package com.example.demo.common.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.*;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class DynamoDbServiceImpl implements DynamoDbService {

    private final DynamoDbClient dynamoDbClient;

    public void createTable(String tableName, String partitionKeyName) {
        try {
            dynamoDbClient.createTable(CreateTableRequest.builder()
                    .tableName(tableName)
                    .keySchema(KeySchemaElement.builder()
                            .attributeName(partitionKeyName)
                            .keyType(KeyType.HASH)
                            .build())
                    .attributeDefinitions(AttributeDefinition.builder()
                            .attributeName(partitionKeyName)
                            .attributeType(ScalarAttributeType.S)
                            .build())
                    .provisionedThroughput(ProvisionedThroughput.builder()
                            .readCapacityUnits(5L)
                            .writeCapacityUnits(5L)
                            .build())
                    .build());
        } catch (ResourceInUseException e) {
            // Ignore if already exists
        }
    }

    public void putItem(String tableName, Map<String, AttributeValue> item) {
        dynamoDbClient.putItem(PutItemRequest.builder()
                .tableName(tableName)
                .item(item)
                .build());
    }

    public Map<String, AttributeValue> getItem(String tableName, String keyName, String keyValue) {
        Map<String, AttributeValue> key = new HashMap<>();
        key.put(keyName, AttributeValue.builder().s(keyValue).build());

        GetItemResponse response = dynamoDbClient.getItem(GetItemRequest.builder()
                .tableName(tableName)
                .key(key)
                .build());

        return response.hasItem() ? response.item() : null;
    }

    public void updateItem(String tableName, Map<String, AttributeValue> item) {
        // For this demo, update is same as put (overwrite)
        dynamoDbClient.putItem(PutItemRequest.builder()
                .tableName(tableName)
                .item(item)
                .build());
    }

    public void deleteItem(String tableName, String keyName, String keyValue) {
        Map<String, AttributeValue> key = new HashMap<>();
        key.put(keyName, AttributeValue.builder().s(keyValue).build());

        dynamoDbClient.deleteItem(DeleteItemRequest.builder()
                .tableName(tableName)
                .key(key)
                .build());
    }
}
