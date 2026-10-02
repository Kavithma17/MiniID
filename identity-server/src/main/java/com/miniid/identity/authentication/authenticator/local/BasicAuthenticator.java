package com.miniid.identity.authentication.authenticator.local;

import com.miniid.identity.authentication.AuthenticationContext;
import com.miniid.identity.authentication.AuthenticationRequest;
import com.miniid.identity.authentication.AuthenticationResult;
import com.miniid.identity.authentication.authenticator.Authenticator;
import com.miniid.identity.core.credential.password.PasswordCredentialService;
import com.miniid.identity.core.user.model.User;
import com.miniid.identity.core.userstore.UserStore;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class BasicAuthenticator implements Authenticator {

    private static final String NAME = "basic";

    private final UserStore userStore;
    private final PasswordCredentialService passwordCredentialService;

    public BasicAuthenticator(
            UserStore userStore,
            PasswordCredentialService passwordCredentialService) {

        this.userStore = userStore;
        this.passwordCredentialService = passwordCredentialService;
    }

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public boolean canHandle(AuthenticationRequest request) {
        return request != null
                && request.getUsername() != null
                && !request.getUsername().isBlank()
                && request.getPassword() != null
                && request.getPassword().length > 0;
    }

    @Override
    public AuthenticationResult authenticate(
            AuthenticationRequest request,
            AuthenticationContext context) {

        if (!canHandle(request)) {
            return AuthenticationResult.failure(
                    "Username and password are required"
            );
        }

        Optional<User> optionalUser =
                userStore.findByUsername(request.getUsername());

        if (optionalUser.isEmpty()) {
            return AuthenticationResult.failure(
                    "Invalid username or password"
            );
        }

        User user = optionalUser.get();

        if (!user.isEnabled() || user.isAccountLocked()) {
            return AuthenticationResult.failure(
                    "Authentication failed"
            );
        }

        char[] password = request.getPassword();

        boolean validPassword =
                passwordCredentialService.verifyPassword(
                        user.getId(),
                        password
                );

        if (!validPassword) {
            return AuthenticationResult.failure(
                    "Invalid username or password"
            );
        }

        context.setUserId(user.getId());
        context.setUsername(user.getUsername());

        return AuthenticationResult.success(
                user.getId(),
                user.getUsername()
        );
    }
}