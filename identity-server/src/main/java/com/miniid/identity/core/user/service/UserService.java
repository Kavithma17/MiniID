package com.miniid.identity.core.user.service;

import com.miniid.identity.core.credential.password.PasswordCredentialService;
import com.miniid.identity.core.user.model.User;
import com.miniid.identity.core.userstore.UserStore;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.Optional;
import java.util.UUID;

@Service
public class UserService {

    private final UserStore userStore;
    private final PasswordCredentialService passwordCredentialService;

    public UserService(
            UserStore userStore,
            PasswordCredentialService passwordCredentialService) {

        this.userStore = userStore;
        this.passwordCredentialService = passwordCredentialService;
    }

    /**
     * Creates a new user together with the user's initial password credential.
     *
     * Both operations execute inside the same transaction.
     */
    @Transactional
    public User createUser(
            String username,
            String email,
            char[] password) {

        validateCreateRequest(username, email, password);

        if (userStore.usernameExists(username)) {
            throw new IllegalArgumentException(
                    "Username already exists"
            );
        }

        if (userStore.emailExists(email)) {
            throw new IllegalArgumentException(
                    "Email already exists"
            );
        }

        try {
            User user = new User(username, email);

            User savedUser = userStore.save(user);

            passwordCredentialService.createPassword(
                    savedUser.getId(),
                    password
            );

            return savedUser;

        } finally {
            clearPassword(password);
        }
    }

    /**
     * Finds a user by their unique ID.
     */
    public Optional<User> findById(UUID userId) {

        if (userId == null) {
            return Optional.empty();
        }

        return userStore.findById(userId);
    }

    /**
     * Finds a user by username.
     */
    public Optional<User> findByUsername(String username) {

        if (username == null || username.isBlank()) {
            return Optional.empty();
        }

        return userStore.findByUsername(username);
    }

    /**
     * Enables an existing user account.
     */
    @Transactional
    public User enableUser(UUID userId) {

        User user = getRequiredUser(userId);

        user.enable();

        return userStore.save(user);
    }

    /**
     * Disables an existing user account.
     */
    @Transactional
    public User disableUser(UUID userId) {

        User user = getRequiredUser(userId);

        user.disable();

        return userStore.save(user);
    }

    /**
     * Locks an existing user account.
     */
    @Transactional
    public User lockUser(UUID userId) {

        User user = getRequiredUser(userId);

        user.lock();

        return userStore.save(user);
    }

    /**
     * Unlocks an existing user account.
     */
    @Transactional
    public User unlockUser(UUID userId) {

        User user = getRequiredUser(userId);

        user.unlock();

        return userStore.save(user);
    }

    /**
     * Returns a user or fails when the requested user does not exist.
     */
    private User getRequiredUser(UUID userId) {

        if (userId == null) {
            throw new IllegalArgumentException(
                    "User ID is required"
            );
        }

        return userStore.findById(userId)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "User not found"
                        )
                );
    }

    /**
     * Validates the minimum information required to create a user.
     */
    private void validateCreateRequest(
            String username,
            String email,
            char[] password) {

        if (username == null || username.isBlank()) {
            throw new IllegalArgumentException(
                    "Username is required"
            );
        }

        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException(
                    "Email is required"
            );
        }

        if (password == null || password.length == 0) {
            throw new IllegalArgumentException(
                    "Password is required"
            );
        }
    }

    /**
     * Removes the supplied plaintext password from the provided array.
     */
    private void clearPassword(char[] password) {

        if (password != null) {
            Arrays.fill(password, '\0');
        }
    }

}