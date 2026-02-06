package com.example.demo.common.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import software.amazon.awssdk.services.secretsmanager.SecretsManagerClient;
import software.amazon.awssdk.services.secretsmanager.model.CreateSecretRequest;
import software.amazon.awssdk.services.secretsmanager.model.GetSecretValueRequest;
import software.amazon.awssdk.services.secretsmanager.model.GetSecretValueResponse;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SecretsManagerServiceImplTest {

    @Mock
    private SecretsManagerClient secretsManagerClient;

    @InjectMocks
    private SecretsManagerServiceImpl secretsManagerService;

    @Test
    void createSecret() {
        secretsManagerService.createSecret("test-secret", "value");
        verify(secretsManagerClient).createSecret(any(CreateSecretRequest.class));
    }

    @Test
    void getSecret() {
        GetSecretValueResponse response = GetSecretValueResponse.builder().secretString("value").build();
        when(secretsManagerClient.getSecretValue(any(GetSecretValueRequest.class))).thenReturn(response);

        String value = secretsManagerService.getSecret("test-secret");
        assertEquals("value", value);
    }
}
