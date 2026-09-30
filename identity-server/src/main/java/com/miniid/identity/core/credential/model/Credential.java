package com.miniid.identity.core.credential.model;

import java.time.Instant;
import java.util.UUID;

public interface Credential {

    UUID getId();

    UUID getUserId();

    CredentialType getType();

    Instant getCreatedAt();

    Instant getUpdatedAt();
}