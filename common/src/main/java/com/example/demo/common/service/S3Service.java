package com.example.demo.common.service;

import java.util.List;

public interface S3Service {
    void createBucket(String bucketName);
    void uploadFile(String bucketName, String key, byte[] content);
    byte[] downloadFile(String bucketName, String key);
    List<String> listFiles(String bucketName);
    void deleteFile(String bucketName, String key);
}
