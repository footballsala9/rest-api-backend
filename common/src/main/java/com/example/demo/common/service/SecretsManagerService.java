package com.example.demo.common.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.secretsmanager.SecretsManagerClient;
import software.amazon.awssdk.services.secretsmanager.model.*;

@Service
@RequiredArgsConstructor
public class SecretsManagerService {

    private final SecretsManagerClient secretsManagerClient;

    public void createSecret(String name, String value) {
        try {
            secretsManagerClient.createSecret(CreateSecretRequest.builder()
                    .name(name)
                    .secretString(value)
                    .build());
        } catch (ResourceExistsException e) {
            // Ignore if already exists
        }
    }

    public String getSecret(String name) {
        try {
            return secretsManagerClient.getSecretValue(GetSecretValueRequest.builder()
                    .secretId(name)
                    .build()).secretString();
        } catch (ResourceNotFoundException e) {
            return null;
        }
    }

    public void updateSecret(String name, String value) {
        try {
            secretsManagerClient.updateSecret(UpdateSecretRequest.builder()
                    .secretId(name)
                    .secretString(value)
                    .build());
        } catch (ResourceNotFoundException e) {
             // If not found, create it
             createSecret(name, value);
        }
    }

    public void deleteSecret(String name) {
        try {
            secretsManagerClient.deleteSecret(DeleteSecretRequest.builder()
                    .secretId(name)
                    .forceDeleteWithoutRecovery(true)
                    .build());
        } catch (ResourceNotFoundException e) {
            // Ignore
        }
    }
}
