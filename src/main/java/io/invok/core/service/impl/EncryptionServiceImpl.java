package io.invok.core.service.impl;

import io.invok.core.service.EncryptionService;
import org.jasypt.encryption.StringEncryptor;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class EncryptionServiceImpl implements EncryptionService {

    private final StringEncryptor stringEncryptor;

    @Override
    public String encrypt(String data) {
        if (data == null) {
            return null;
        }
        return stringEncryptor.encrypt(data);
    }

    @Override
    public String decrypt(String encryptedData) {
        if (encryptedData == null) {
            return null;
        }
        return stringEncryptor.decrypt(encryptedData);
    }
}
