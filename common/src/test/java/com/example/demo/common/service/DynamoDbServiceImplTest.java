package com.example.demo.common.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.*;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DynamoDbServiceImplTest {

    @Mock
    private DynamoDbClient dynamoDbClient;

    @InjectMocks
    private DynamoDbServiceImpl dynamoDbService;

    @Test
    void createTable() {
        dynamoDbService.createTable("test-table", "id");
        verify(dynamoDbClient).createTable(any(CreateTableRequest.class));
    }

    @Test
    void putItem() {
        dynamoDbService.putItem("test-table", new HashMap<>());
        verify(dynamoDbClient).putItem(any(PutItemRequest.class));
    }

    @Test
    void getItem() {
        Map<String, AttributeValue> item = new HashMap<>();
        item.put("id", AttributeValue.builder().s("1").build());
        GetItemResponse response = GetItemResponse.builder().item(item).build();
        when(dynamoDbClient.getItem(any(GetItemRequest.class))).thenReturn(response);

        Map<String, AttributeValue> result = dynamoDbService.getItem("test-table", "id", "1");
        assertNotNull(result);
    }
}
