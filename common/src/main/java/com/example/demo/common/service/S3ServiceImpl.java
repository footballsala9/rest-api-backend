package com.example.demo.common.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class S3ServiceImpl implements S3Service {

    private final S3Client s3Client;

    public void createBucket(String bucketName) {
        try {
            s3Client.createBucket(CreateBucketRequest.builder().bucket(bucketName).build());
        } catch (BucketAlreadyExistsException | BucketAlreadyOwnedByYouException e) {
            // Ignore if already exists
        }
    }

    public void uploadFile(String bucketName, String key, byte[] content) {
        s3Client.putObject(PutObjectRequest.builder().bucket(bucketName).key(key).build(),
                RequestBody.fromBytes(content));
    }

    public byte[] downloadFile(String bucketName, String key) {
        try {
            return s3Client.getObject(GetObjectRequest.builder().bucket(bucketName).key(key).build())
                    .readAllBytes();
        } catch (IOException e) {
            throw new RuntimeException("Failed to read file content", e);
        }
    }

    public List<String> listFiles(String bucketName) {
        return s3Client.listObjectsV2(ListObjectsV2Request.builder().bucket(bucketName).build())
                .contents().stream()
                .map(S3Object::key)
                .collect(Collectors.toList());
    }

    public void deleteFile(String bucketName, String key) {
        s3Client.deleteObject(DeleteObjectRequest.builder().bucket(bucketName).key(key).build());
    }
}
