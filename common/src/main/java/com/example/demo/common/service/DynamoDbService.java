package com.example.demo.common.service;

import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

import java.util.Map;

public interface DynamoDbService {
    void createTable(String tableName, String partitionKeyName);
    void putItem(String tableName, Map<String, AttributeValue> item);
    Map<String, AttributeValue> getItem(String tableName, String keyName, String keyValue);
    void updateItem(String tableName, Map<String, AttributeValue> item);
    void deleteItem(String tableName, String keyName, String keyValue);
}
