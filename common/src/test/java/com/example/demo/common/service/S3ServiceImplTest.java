package com.example.demo.common.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import software.amazon.awssdk.core.ResponseBytes;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class S3ServiceImplTest {

    @Mock
    private S3Client s3Client;

    @InjectMocks
    private S3ServiceImpl s3Service;

    @Test
    void createBucket() {
        s3Service.createBucket("test-bucket");
        verify(s3Client).createBucket(any(CreateBucketRequest.class));
    }

    @Test
    void uploadFile() {
        s3Service.uploadFile("test-bucket", "key", "content".getBytes());
        verify(s3Client).putObject(any(PutObjectRequest.class), any(RequestBody.class));
    }

    @Test
    void listFiles() {
        S3Object s3Object = S3Object.builder().key("file.txt").build();
        ListObjectsV2Response response = ListObjectsV2Response.builder().contents(s3Object).build();
        when(s3Client.listObjectsV2(any(ListObjectsV2Request.class))).thenReturn(response);

        List<String> files = s3Service.listFiles("test-bucket");
        assertEquals(1, files.size());
        assertEquals("file.txt", files.get(0));
    }
}
