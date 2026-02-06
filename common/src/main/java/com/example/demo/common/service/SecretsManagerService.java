package com.example.demo.common.service;

public interface SecretsManagerService {
    void createSecret(String name, String value);
    String getSecret(String name);
    void updateSecret(String name, String value);
    void deleteSecret(String name);
}
