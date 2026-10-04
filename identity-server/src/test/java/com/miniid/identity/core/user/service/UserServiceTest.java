package com.miniid.identity.core.user.service;

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
class UserServiceTest {

    @Mock
    private UserStore userStore;

    @Mock
    private PasswordCredentialService passwordCredentialService;

    private UserService userService;

    @BeforeEach
    void setUp() {
        userService = new UserService(
                userStore,
                passwordCredentialService
        );
    }

    @Test
    void shouldCreateUserWithPasswordCredential() throws Exception {

        UUID userId = UUID.randomUUID();

        when(userStore.usernameExists("alice"))
                .thenReturn(false);

        when(userStore.emailExists("alice@example.com"))
                .thenReturn(false);

        when(userStore.save(any(User.class)))
                .thenAnswer(invocation -> {
                    User user = invocation.getArgument(0);
                    setUserId(user, userId);
                    return user;
                });

        char[] password = "secret-password".toCharArray();

        User result = userService.createUser(
                "alice",
                "alice@example.com",
                password
        );

        assertEquals(userId, result.getId());
        assertEquals("alice", result.getUsername());
        assertEquals("alice@example.com", result.getEmail());

        verify(userStore).save(any(User.class));

        verify(passwordCredentialService)
                .createPassword(
                        eq(userId),
                        any(char[].class)
                );
    }

    @Test
    void shouldRejectDuplicateUsername() {

        when(userStore.usernameExists("alice"))
                .thenReturn(true);

        char[] password = "password".toCharArray();

        assertThrows(
                IllegalArgumentException.class,
                () -> userService.createUser(
                        "alice",
                        "alice@example.com",
                        password
                )
        );

        verify(userStore, never()).save(any());
        verifyNoInteractions(passwordCredentialService);
    }

    @Test
    void shouldRejectDuplicateEmail() {

        when(userStore.usernameExists("alice"))
                .thenReturn(false);

        when(userStore.emailExists("alice@example.com"))
                .thenReturn(true);

        char[] password = "password".toCharArray();

        assertThrows(
                IllegalArgumentException.class,
                () -> userService.createUser(
                        "alice",
                        "alice@example.com",
                        password
                )
        );

        verify(userStore, never()).save(any());
        verifyNoInteractions(passwordCredentialService);
    }

    @Test
    void shouldRejectBlankUsername() {

        assertThrows(
                IllegalArgumentException.class,
                () -> userService.createUser(
                        " ",
                        "alice@example.com",
                        "password".toCharArray()
                )
        );

        verifyNoInteractions(userStore);
        verifyNoInteractions(passwordCredentialService);
    }

    @Test
    void shouldRejectBlankEmail() {

        assertThrows(
                IllegalArgumentException.class,
                () -> userService.createUser(
                        "alice",
                        " ",
                        "password".toCharArray()
                )
        );

        verifyNoInteractions(userStore);
        verifyNoInteractions(passwordCredentialService);
    }

    @Test
    void shouldRejectEmptyPassword() {

        assertThrows(
                IllegalArgumentException.class,
                () -> userService.createUser(
                        "alice",
                        "alice@example.com",
                        new char[0]
                )
        );

        verifyNoInteractions(userStore);
        verifyNoInteractions(passwordCredentialService);
    }

    private void setUserId(User user, UUID userId) throws Exception {

        Field idField = User.class.getDeclaredField("id");
        idField.setAccessible(true);
        idField.set(user, userId);
    }

@Test
void shouldFindUserById() throws Exception {

    UUID userId = UUID.randomUUID();
    User user = new User("alice", "alice@example.com");
    setUserId(user, userId);

    when(userStore.findById(userId))
            .thenReturn(Optional.of(user));

    Optional<User> result =
            userService.findById(userId);

    assertTrue(result.isPresent());
    assertEquals(userId, result.get().getId());
    assertEquals("alice", result.get().getUsername());

    verify(userStore).findById(userId);
}

@Test
void shouldFindUserByUsername() {

    User user = new User(
            "alice",
            "alice@example.com"
    );

    when(userStore.findByUsername("alice"))
            .thenReturn(Optional.of(user));

    Optional<User> result =
            userService.findByUsername("alice");

    assertTrue(result.isPresent());
    assertEquals("alice", result.get().getUsername());

    verify(userStore).findByUsername("alice");
}

@Test
void shouldDisableUser() throws Exception {

    UUID userId = UUID.randomUUID();
    User user = new User("alice", "alice@example.com");
    setUserId(user, userId);

    when(userStore.findById(userId))
            .thenReturn(Optional.of(user));

    when(userStore.save(user))
            .thenReturn(user);

    User result =
            userService.disableUser(userId);

    assertFalse(result.isEnabled());

    verify(userStore).findById(userId);
    verify(userStore).save(user);
}

@Test
void shouldEnableUser() throws Exception {

    UUID userId = UUID.randomUUID();
    User user = new User("alice", "alice@example.com");
    setUserId(user, userId);

    user.disable();

    when(userStore.findById(userId))
            .thenReturn(Optional.of(user));

    when(userStore.save(user))
            .thenReturn(user);

    User result =
            userService.enableUser(userId);

    assertTrue(result.isEnabled());

    verify(userStore).findById(userId);
    verify(userStore).save(user);
}

@Test
void shouldLockUser() throws Exception {

    UUID userId = UUID.randomUUID();
    User user = new User("alice", "alice@example.com");
    setUserId(user, userId);

    when(userStore.findById(userId))
            .thenReturn(Optional.of(user));

    when(userStore.save(user))
            .thenReturn(user);

    User result =
            userService.lockUser(userId);

    assertTrue(result.isAccountLocked());

    verify(userStore).findById(userId);
    verify(userStore).save(user);
}

@Test
void shouldUnlockUser() throws Exception {

    UUID userId = UUID.randomUUID();
    User user = new User("alice", "alice@example.com");
    setUserId(user, userId);

    user.lock();

    when(userStore.findById(userId))
            .thenReturn(Optional.of(user));

    when(userStore.save(user))
            .thenReturn(user);

    User result =
            userService.unlockUser(userId);

    assertFalse(result.isAccountLocked());

    verify(userStore).findById(userId);
    verify(userStore).save(user);
}

@Test
void shouldFailWhenManagingUnknownUser() {

    UUID userId = UUID.randomUUID();

    when(userStore.findById(userId))
            .thenReturn(Optional.empty());

    IllegalArgumentException exception =
            assertThrows(
                    IllegalArgumentException.class,
                    () -> userService.disableUser(userId)
            );

    assertEquals(
            "User not found",
            exception.getMessage()
    );

    verify(userStore).findById(userId);
    verify(userStore, never()).save(any());
}
    
}

