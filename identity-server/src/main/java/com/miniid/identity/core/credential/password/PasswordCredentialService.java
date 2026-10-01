package com.miniid.identity.core.credential.password;

import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Optional;
import java.util.UUID;

@Service
public class PasswordCredentialService {

    private final PasswordCredentialRepository credentialRepository;
    private final PasswordHasher passwordHasher;

    public PasswordCredentialService(
            PasswordCredentialRepository credentialRepository,
            PasswordHasher passwordHasher) {

        this.credentialRepository = credentialRepository;
        this.passwordHasher = passwordHasher;
    }

    public void createPassword(UUID userId, char[] password) {
        try {
            if (credentialRepository.findByUserId(userId).isPresent()) {
                throw new IllegalStateException(
                        "Password credential already exists for user"
                );
            }

            String passwordHash = passwordHasher.hash(password);

            PasswordCredential credential =
                    new PasswordCredential(userId, passwordHash);

            credentialRepository.save(credential);

        } finally {
            clearPassword(password);
        }
    }

    public boolean verifyPassword(UUID userId, char[] password) {
        try {
            Optional<PasswordCredential> credential =
                    credentialRepository.findByUserId(userId);

            if (credential.isEmpty()) {
                return false;
            }

            return passwordHasher.verify(
                    password,
                    credential.get().getPasswordHash()
            );

        } finally {
            clearPassword(password);
        }
    }

    private void clearPassword(char[] password) {
        if (password != null) {
            Arrays.fill(password, '\0');
        }
    }
}