package com.miniid.identity.authentication.authenticator.local;

import com.miniid.identity.authentication.AuthenticationContext;
import com.miniid.identity.authentication.AuthenticationRequest;
import com.miniid.identity.authentication.AuthenticationResult;
import com.miniid.identity.authentication.AuthenticationStatus;
import com.miniid.identity.core.credential.password.PasswordCredentialService;
import com.miniid.identity.core.user.model.User;
import com.miniid.identity.core.userstore.UserStore;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Field;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BasicAuthenticatorTest {

    @Mock
    private UserStore userStore;

    @Mock
    private PasswordCredentialService passwordCredentialService;

    private BasicAuthenticator authenticator;

    @BeforeEach
    void setUp() {
        authenticator = new BasicAuthenticator(
                userStore,
                passwordCredentialService
        );
    }

    @Test
    void shouldAuthenticateValidUser() throws Exception {

        User user = createUser();

        when(userStore.findByUsername("alice"))
                .thenReturn(Optional.of(user));

        when(passwordCredentialService.verifyPassword(
                eq(user.getId()),
                any(char[].class)
        )).thenReturn(true);

        AuthenticationRequest request =
                new AuthenticationRequest(
                        "alice",
                        "correct-password".toCharArray()
                );

        AuthenticationContext context =
                new AuthenticationContext();

        AuthenticationResult result =
                authenticator.authenticate(request, context);

        assertEquals(
                AuthenticationStatus.SUCCESS,
                result.getStatus()
        );

        assertEquals(user.getId(), result.getUserId());
        assertEquals("alice", result.getUsername());

        assertEquals(user.getId(), context.getUserId());
        assertEquals("alice", context.getUsername());

        request.clearCredentials();
    }

    @Test
    void shouldFailForUnknownUser() {

        when(userStore.findByUsername("unknown"))
                .thenReturn(Optional.empty());

        AuthenticationRequest request =
                new AuthenticationRequest(
                        "unknown",
                        "password".toCharArray()
                );

        AuthenticationResult result =
                authenticator.authenticate(
                        request,
                        new AuthenticationContext()
                );

        assertEquals(
                AuthenticationStatus.FAILURE,
                result.getStatus()
        );

        assertFalse(result.isSuccessful());

        verifyNoInteractions(passwordCredentialService);

        request.clearCredentials();
    }

    @Test
    void shouldFailForWrongPassword() throws Exception {

        User user = createUser();

        when(userStore.findByUsername("alice"))
                .thenReturn(Optional.of(user));

        when(passwordCredentialService.verifyPassword(
                eq(user.getId()),
                any(char[].class)
        )).thenReturn(false);

        AuthenticationRequest request =
                new AuthenticationRequest(
                        "alice",
                        "wrong-password".toCharArray()
                );

        AuthenticationResult result =
                authenticator.authenticate(
                        request,
                        new AuthenticationContext()
                );

        assertEquals(
                AuthenticationStatus.FAILURE,
                result.getStatus()
        );

        request.clearCredentials();
    }

    @Test
    void shouldFailForDisabledUser() throws Exception {

        User user = createUser();
        user.disable();

        when(userStore.findByUsername("alice"))
                .thenReturn(Optional.of(user));

        AuthenticationRequest request =
                new AuthenticationRequest(
                        "alice",
                        "password".toCharArray()
                );

        AuthenticationResult result =
                authenticator.authenticate(
                        request,
                        new AuthenticationContext()
                );

        assertEquals(
                AuthenticationStatus.FAILURE,
                result.getStatus()
        );

        verifyNoInteractions(passwordCredentialService);

        request.clearCredentials();
    }

    @Test
    void shouldFailForLockedUser() throws Exception {

        User user = createUser();
        user.lock();

        when(userStore.findByUsername("alice"))
                .thenReturn(Optional.of(user));

        AuthenticationRequest request =
                new AuthenticationRequest(
                        "alice",
                        "password".toCharArray()
                );

        AuthenticationResult result =
                authenticator.authenticate(
                        request,
                        new AuthenticationContext()
                );

        assertEquals(
                AuthenticationStatus.FAILURE,
                result.getStatus()
        );

        verifyNoInteractions(passwordCredentialService);

        request.clearCredentials();
    }

    private User createUser() throws Exception {

        User user = new User(
                "alice",
                "alice@example.com"
        );

        UUID userId = UUID.randomUUID();

        Field idField = User.class.getDeclaredField("id");
        idField.setAccessible(true);
        idField.set(user, userId);

        return user;
    }
}