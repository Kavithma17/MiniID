package com.miniid.identity.authentication;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class AuthenticationContext {

    private final UUID contextId;

    private UUID userId;
    private String username;

    private final Map<String, Object> properties;

    public AuthenticationContext() {
        this.contextId = UUID.randomUUID();
        this.properties = new HashMap<>();
    }

    public UUID getContextId() {
        return contextId;
    }

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setProperty(String name, Object value) {
        properties.put(name, value);
    }

    public Object getProperty(String name) {
        return properties.get(name);
    }
}