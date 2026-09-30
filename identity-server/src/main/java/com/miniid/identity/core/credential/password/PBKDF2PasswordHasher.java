package com.miniid.identity.core.credential.password;

import org.springframework.stereotype.Component;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Base64;

@Component
public class PBKDF2PasswordHasher implements PasswordHasher {

    private static final String ALGORITHM = "PBKDF2WithHmacSHA256";
    private static final int ITERATIONS = 600_000;
    private static final int SALT_LENGTH = 16;
    private static final int KEY_LENGTH = 256;

    private final SecureRandom secureRandom = new SecureRandom();

    @Override
    public String hash(char[] password) {

        byte[] salt = new byte[SALT_LENGTH];
        secureRandom.nextBytes(salt);

        byte[] hash = deriveKey(password, salt, ITERATIONS, KEY_LENGTH);

        return ITERATIONS
                + ":"
                + Base64.getEncoder().encodeToString(salt)
                + ":"
                + Base64.getEncoder().encodeToString(hash);
    }

    @Override
    public boolean verify(char[] password, String encodedHash) {

        String[] parts = encodedHash.split(":");

        if (parts.length != 3) {
            return false;
        }

        try {
            int iterations = Integer.parseInt(parts[0]);

            byte[] salt = Base64.getDecoder().decode(parts[1]);
            byte[] expectedHash = Base64.getDecoder().decode(parts[2]);

            byte[] actualHash = deriveKey(
                    password,
                    salt,
                    iterations,
                    expectedHash.length * 8
            );

            return MessageDigest.isEqual(expectedHash, actualHash);

        } catch (IllegalArgumentException exception) {
            return false;
        }
    }

    private byte[] deriveKey(
            char[] password,
            byte[] salt,
            int iterations,
            int keyLength
    ) {

        PBEKeySpec spec = new PBEKeySpec(
                password,
                salt,
                iterations,
                keyLength
        );

        try {
            SecretKeyFactory factory =
                    SecretKeyFactory.getInstance(ALGORITHM);

            return factory.generateSecret(spec).getEncoded();

        } catch (Exception exception) {
            throw new IllegalStateException(
                    "Failed to hash password",
                    exception
            );

        } finally {
            spec.clearPassword();
        }
    }
}