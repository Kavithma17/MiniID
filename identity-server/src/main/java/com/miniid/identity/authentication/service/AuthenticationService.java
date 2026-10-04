//how authentication flow works 
package com.miniid.identity.authentication.service;

import com.miniid.identity.authentication.AuthenticationContext;
import com.miniid.identity.authentication.AuthenticationRequest;
import com.miniid.identity.authentication.AuthenticationResult;
import com.miniid.identity.authentication.authenticator.Authenticator;
import com.miniid.identity.authentication.flow.AuthenticationFlow;
import org.springframework.stereotype.Service;

@Service
public class AuthenticationService {

    public AuthenticationResult authenticate(
            AuthenticationRequest request,
            AuthenticationFlow flow) {

        if (request == null) {
            throw new IllegalArgumentException(
                    "Authentication request cannot be null"
            );
        }

        if (flow == null) {
            throw new IllegalArgumentException(
                    "Authentication flow cannot be null"
            );
        }

        AuthenticationContext context =
                new AuthenticationContext();

        try {
            for (Authenticator authenticator : flow.getAuthenticators()) {

                if (!authenticator.canHandle(request)) {
                    continue;
                }

                AuthenticationResult result =
                        authenticator.authenticate(request, context);

                if (!result.isSuccessful()) {
                    return result;
                }
            }

            if (context.getUserId() == null) {
                return AuthenticationResult.failure(
                        "No authenticator handled the request"
                );
            }

            return AuthenticationResult.success(
                    context.getUserId(),
                    context.getUsername()
            );

        } finally {
            request.clearCredentials();
        }
    }
}