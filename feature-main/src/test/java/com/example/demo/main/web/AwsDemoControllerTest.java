package com.example.demo.main.web;

import com.example.demo.common.service.DynamoDbService;
import com.example.demo.common.service.S3Service;
import com.example.demo.common.service.SecretsManagerService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AwsDemoController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(AwsDemoController.class)
public class AwsDemoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private S3Service s3Service;

    @MockitoBean
    private DynamoDbService dynamoDbService;

    @MockitoBean
    private SecretsManagerService secretsManagerService;

    @Test
    void testCreateBucket() throws Exception {
        mockMvc.perform(post("/api/aws/s3/buckets")
                .param("bucketName", "test-bucket"))
                .andExpect(status().isOk());
        verify(s3Service).createBucket("test-bucket");
    }

    @Test
    void testUploadFile() throws Exception {
        MockMultipartFile file = new MockMultipartFile("file", "test.txt", "text/plain", "hello".getBytes());
        mockMvc.perform(multipart("/api/aws/s3/files")
                .file(file)
                .param("bucketName", "test-bucket")
                .param("key", "test.txt"))
                .andExpect(status().isOk());
        verify(s3Service).uploadFile(eq("test-bucket"), eq("test.txt"), any());
    }

    @Test
    void testPutItem() throws Exception {
        String json = "{\"key\": \"value\"}";
        mockMvc.perform(post("/api/aws/dynamo/items")
                .param("tableName", "test-table")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isOk());
        verify(dynamoDbService).putItem(eq("test-table"), anyMap());
    }

    @Test
    void testCreateSecret() throws Exception {
        mockMvc.perform(post("/api/aws/secrets")
                .param("name", "test-secret")
                .content("secret-value"))
                .andExpect(status().isOk());
        verify(secretsManagerService).createSecret("test-secret", "secret-value");
    }
}
