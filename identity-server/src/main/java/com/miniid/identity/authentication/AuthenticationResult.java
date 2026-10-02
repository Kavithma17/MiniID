package com.miniid.identity.authentication;

import java.util.UUID;

public class AuthenticationResult {

    private final AuthenticationStatus status;
    private final UUID userId;
    private final String username;
    private final String message;

    private AuthenticationResult(
            AuthenticationStatus status,
            UUID userId,
            String username,
            String message) {

        this.status = status;
        this.userId = userId;
        this.username = username;
        this.message = message;
    }

    public static AuthenticationResult success(
            UUID userId,
            String username) {

        return new AuthenticationResult(
                AuthenticationStatus.SUCCESS,
                userId,
                username,
                null
        );
    }

    public static AuthenticationResult failure(String message) {

        return new AuthenticationResult(
                AuthenticationStatus.FAILURE,
                null,
                null,
                message
        );
    }

    public static AuthenticationResult incomplete(String message) {

        return new AuthenticationResult(
                AuthenticationStatus.INCOMPLETE,
                null,
                null,
                message
        );
    }

    public AuthenticationStatus getStatus() {
        return status;
    }

    public UUID getUserId() {
        return userId;
    }

    public String getUsername() {
        return username;
    }

    public String getMessage() {
        return message;
    }

    public boolean isSuccessful() {
        return status == AuthenticationStatus.SUCCESS;
    }
}