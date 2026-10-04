package com.miniid.identity.authentication.flow;

import com.miniid.identity.authentication.authenticator.Authenticator;

import java.util.List;

public class AuthenticationFlow {

    private final List<Authenticator> authenticators;

    public AuthenticationFlow(List<Authenticator> authenticators) {
        if (authenticators == null || authenticators.isEmpty()) {
            throw new IllegalArgumentException(
                    "Authentication flow must contain at least one authenticator"
            );
        }

        this.authenticators = List.copyOf(authenticators);
    }

    public List<Authenticator> getAuthenticators() {
        return authenticators;
    }
}