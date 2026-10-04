package com.miniid.identity.application.model;

import jakarta.persistence.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(
        name = "application_redirect_uris",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_application_redirect_uri",
                        columnNames = {
                                "application_id",
                                "redirect_uri"
                        }
                )
        }
)
public class ApplicationRedirectUri {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(
            name = "application_id",
            nullable = false
    )
    private UUID applicationId;

    @Column(
            name = "redirect_uri",
            nullable = false,
            length = 2048
    )
    private String redirectUri;

    @Column(
            name = "created_at",
            nullable = false,
            updatable = false
    )
    private Instant createdAt;

    protected ApplicationRedirectUri() {
    }

    public ApplicationRedirectUri(
            UUID applicationId,
            String redirectUri) {

        this.applicationId = applicationId;
        this.redirectUri = redirectUri;
    }

    @PrePersist
    protected void onCreate() {
        createdAt = Instant.now();
    }

    public UUID getId() {
        return id;
    }

    public UUID getApplicationId() {
        return applicationId;
    }

    public String getRedirectUri() {
        return redirectUri;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}