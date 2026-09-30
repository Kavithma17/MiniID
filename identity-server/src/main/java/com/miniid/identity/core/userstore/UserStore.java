package com.miniid.identity.core.userstore;

import com.miniid.identity.core.user.model.User;

import java.util.Optional;
import java.util.UUID;

public interface UserStore {

    Optional<User> findById(UUID id);

    Optional<User> findByUsername(String username);

    Optional<User> findByEmail(String email);

    boolean usernameExists(String username);

    boolean emailExists(String email);

    User save(User user);
}