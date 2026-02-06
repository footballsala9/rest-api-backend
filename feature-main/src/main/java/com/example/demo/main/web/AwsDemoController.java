package com.example.demo.main.web;

import com.example.demo.common.service.DynamoDbService;
import com.example.demo.common.service.S3Service;
import com.example.demo.common.service.SecretsManagerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/aws")
@RequiredArgsConstructor
public class AwsDemoController {

    private final S3Service s3Service;
    private final DynamoDbService dynamoDbService;
    private final SecretsManagerService secretsManagerService;

    // S3 Endpoints
    @PostMapping("/s3/buckets")
    public ResponseEntity<Void> createBucket(@RequestParam String bucketName) {
        s3Service.createBucket(bucketName);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/s3/files")
    public ResponseEntity<Void> uploadFile(@RequestParam String bucketName,
                                           @RequestParam String key,
                                           @RequestParam("file") MultipartFile file) throws IOException {
        s3Service.uploadFile(bucketName, key, file.getBytes());
        return ResponseEntity.ok().build();
    }

    @GetMapping("/s3/files/{key}")
    public ResponseEntity<byte[]> downloadFile(@RequestParam String bucketName, @PathVariable String key) {
        return ResponseEntity.ok(s3Service.downloadFile(bucketName, key));
    }

    @GetMapping("/s3/files")
    public ResponseEntity<List<String>> listFiles(@RequestParam String bucketName) {
        return ResponseEntity.ok(s3Service.listFiles(bucketName));
    }

    @DeleteMapping("/s3/files/{key}")
    public ResponseEntity<Void> deleteFile(@RequestParam String bucketName, @PathVariable String key) {
        s3Service.deleteFile(bucketName, key);
        return ResponseEntity.noContent().build();
    }

    // DynamoDB Endpoints
    @PostMapping("/dynamo/tables")
    public ResponseEntity<Void> createTable(@RequestParam String tableName, @RequestParam String partitionKey) {
        dynamoDbService.createTable(tableName, partitionKey);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/dynamo/items")
    public ResponseEntity<Void> putItem(@RequestParam String tableName, @RequestBody Map<String, String> item) {
        Map<String, AttributeValue> dynamoItem = item.entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey, e -> AttributeValue.builder().s(e.getValue()).build()));
        dynamoDbService.putItem(tableName, dynamoItem);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/dynamo/items/{id}")
    public ResponseEntity<Map<String, String>> getItem(@RequestParam String tableName,
                                                       @RequestParam String keyName,
                                                       @PathVariable String id) {
        Map<String, AttributeValue> item = dynamoDbService.getItem(tableName, keyName, id);
        if (item == null) {
            return ResponseEntity.notFound().build();
        }
        Map<String, String> result = item.entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey, e -> e.getValue().s()));
        return ResponseEntity.ok(result);
    }

    @DeleteMapping("/dynamo/items/{id}")
    public ResponseEntity<Void> deleteItem(@RequestParam String tableName,
                                           @RequestParam String keyName,
                                           @PathVariable String id) {
        dynamoDbService.deleteItem(tableName, keyName, id);
        return ResponseEntity.noContent().build();
    }

    // Secrets Manager Endpoints
    @PostMapping("/secrets")
    public ResponseEntity<Void> createSecret(@RequestParam String name, @RequestBody String value) {
        secretsManagerService.createSecret(name, value);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/secrets/{name}")
    public ResponseEntity<String> getSecret(@PathVariable String name) {
        String secret = secretsManagerService.getSecret(name);
        if (secret == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(secret);
    }

    @DeleteMapping("/secrets/{name}")
    public ResponseEntity<Void> deleteSecret(@PathVariable String name) {
        secretsManagerService.deleteSecret(name);
        return ResponseEntity.noContent().build();
    }
}
