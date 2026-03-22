package com.vault.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

@Component
public class AesEncryptionUtil {

    @Value("${encryption.key}")
    private String secretKey;
    // This key comes from application.properties
    // Must be EXACTLY 32 characters = AES-256 bit encryption

    // ── ENCRYPT ──────────────────────────────────────────
    // Takes file bytes → returns encrypted Base64 string
    // This Base64 string is what gets saved in MySQL
    public String encrypt(byte[] fileBytes) throws Exception {

        // Step 1: Create the secret key object
        SecretKeySpec keySpec = new SecretKeySpec(
            secretKey.getBytes(), "AES"
        );

        // Step 2: Create AES cipher in encrypt mode
        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, keySpec);

        // Step 3: Encrypt the file bytes
        byte[] encryptedBytes = cipher.doFinal(fileBytes);

        // Step 4: Convert encrypted bytes to Base64 string
        // Base64 makes binary data safe to store as text in MySQL
        return Base64.getEncoder().encodeToString(encryptedBytes);
    }

    // ── DECRYPT ──────────────────────────────────────────
    // Takes encrypted Base64 string → returns original file bytes
    // Called when user downloads a file
    public byte[] decrypt(String encryptedData) throws Exception {

        // Step 1: Create the same secret key object
        SecretKeySpec keySpec = new SecretKeySpec(
            secretKey.getBytes(), "AES"
        );

        // Step 2: Create AES cipher in DECRYPT mode
        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
        cipher.init(Cipher.DECRYPT_MODE, keySpec);

        // Step 3: Convert Base64 string back to encrypted bytes
        byte[] encryptedBytes = Base64.getDecoder().decode(encryptedData);

        // Step 4: Decrypt → get original file bytes back
        return cipher.doFinal(encryptedBytes);
    }
}