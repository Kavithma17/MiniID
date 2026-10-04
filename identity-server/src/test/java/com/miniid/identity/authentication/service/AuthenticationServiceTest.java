package com.miniid.identity.authentication.service;

import com.miniid.identity.authentication.AuthenticationContext;
import com.miniid.identity.authentication.AuthenticationRequest;
import com.miniid.identity.authentication.AuthenticationResult;
import com.miniid.identity.authentication.authenticator.Authenticator;
import com.miniid.identity.authentication.flow.AuthenticationFlow;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class AuthenticationServiceTest {

    private AuthenticationService authenticationService;

    @BeforeEach
    void setUp() {
        authenticationService = new AuthenticationService();
    }

    @Test
    void shouldReturnSuccessWhenAuthenticatorSucceeds() {

        UUID userId = UUID.randomUUID();

        Authenticator authenticator = new Authenticator() {

            @Override
            public String getName() {
                return "test";
            }

            @Override
            public boolean canHandle(AuthenticationRequest request) {
                return true;
            }

            @Override
            public AuthenticationResult authenticate(
                    AuthenticationRequest request,
                    AuthenticationContext context) {

                context.setUserId(userId);
                context.setUsername("alice");

                return AuthenticationResult.success(
                        userId,
                        "alice"
                );
            }
        };

        AuthenticationFlow flow =
                new AuthenticationFlow(List.of(authenticator));

        AuthenticationRequest request =
                new AuthenticationRequest(
                        "alice",
                        "password".toCharArray()
                );

        AuthenticationResult result =
                authenticationService.authenticate(request, flow);

        assertTrue(result.isSuccessful());
        assertEquals(userId, result.getUserId());
        assertEquals("alice", result.getUsername());
    }

    @Test
    void shouldReturnFailureWhenAuthenticatorFails() {

        Authenticator authenticator = new Authenticator() {

            @Override
            public String getName() {
                return "test";
            }

            @Override
            public boolean canHandle(AuthenticationRequest request) {
                return true;
            }

            @Override
            public AuthenticationResult authenticate(
                    AuthenticationRequest request,
                    AuthenticationContext context) {

                return AuthenticationResult.failure(
                        "Authentication failed"
                );
            }
        };

        AuthenticationFlow flow =
                new AuthenticationFlow(List.of(authenticator));

        AuthenticationRequest request =
                new AuthenticationRequest(
                        "alice",
                        "wrong-password".toCharArray()
                );

        AuthenticationResult result =
                authenticationService.authenticate(request, flow);

        assertFalse(result.isSuccessful());
    }

    @Test
    void shouldFailWhenNoAuthenticatorCanHandleRequest() {

        Authenticator authenticator = new Authenticator() {

            @Override
            public String getName() {
                return "test";
            }

            @Override
            public boolean canHandle(AuthenticationRequest request) {
                return false;
            }

            @Override
            public AuthenticationResult authenticate(
                    AuthenticationRequest request,
                    AuthenticationContext context) {

                throw new AssertionError(
                        "Authenticator should not have been called"
                );
            }
        };

        AuthenticationFlow flow =
                new AuthenticationFlow(List.of(authenticator));

        AuthenticationRequest request =
                new AuthenticationRequest(
                        "alice",
                        "password".toCharArray()
                );

        AuthenticationResult result =
                authenticationService.authenticate(request, flow);

        assertFalse(result.isSuccessful());

        assertEquals(
                "No authenticator handled the request",
                result.getMessage()
        );
    }

    @Test
    void shouldClearCredentialsAfterAuthentication() {

        Authenticator authenticator = new Authenticator() {

            @Override
            public String getName() {
                return "test";
            }

            @Override
            public boolean canHandle(AuthenticationRequest request) {
                return true;
            }

            @Override
            public AuthenticationResult authenticate(
                    AuthenticationRequest request,
                    AuthenticationContext context) {

                context.setUserId(UUID.randomUUID());
                context.setUsername("alice");

                return AuthenticationResult.success(
                        context.getUserId(),
                        context.getUsername()
                );
            }
        };

        AuthenticationFlow flow =
                new AuthenticationFlow(List.of(authenticator));

        AuthenticationRequest request =
                new AuthenticationRequest(
                        "alice",
                        "secret".toCharArray()
                );

        authenticationService.authenticate(request, flow);

        char[] passwordAfterAuthentication =
                request.getPassword();

        assertNotNull(passwordAfterAuthentication);

        for (char character : passwordAfterAuthentication) {
            assertEquals('\0', character);
        }
    }
}