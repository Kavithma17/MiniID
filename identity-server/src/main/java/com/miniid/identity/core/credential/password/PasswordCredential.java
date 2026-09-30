package com.miniid.identity.core.credential.password;

import com.miniid.identity.core.credential.model.Credential;
import com.miniid.identity.core.credential.model.CredentialType;
import jakarta.persistence.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(
        name = "password_credentials",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_password_credentials_user",
                        columnNames = "user_id"
                )
        }
)
public class PasswordCredential implements Credential {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Column(name = "password_hash", nullable = false, length = 1024)
    private String passwordHash;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    protected PasswordCredential() {
    }

    public PasswordCredential(UUID userId, String passwordHash) {
        this.userId = userId;
        this.passwordHash = passwordHash;
    }

    @PrePersist
    protected void onCreate() {
        Instant now = Instant.now();
        createdAt = now;
        updatedAt = now;
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = Instant.now();
    }

    @Override
    public UUID getId() {
        return id;
    }

    @Override
    public UUID getUserId() {
        return userId;
    }

    @Override
    public CredentialType getType() {
        return CredentialType.PASSWORD;
    }

    @Override
    public Instant getCreatedAt() {
        return createdAt;
    }

    @Override
    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void updatePasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }
}