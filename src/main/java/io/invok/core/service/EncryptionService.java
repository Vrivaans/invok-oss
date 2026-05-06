package io.invok.core.service;

public interface EncryptionService {
    String encrypt(String data);

    String decrypt(String encryptedData);
}
