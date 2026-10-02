package com.miniid.identity.authentication.authenticator;

import com.miniid.identity.authentication.AuthenticationContext;
import com.miniid.identity.authentication.AuthenticationRequest;
import com.miniid.identity.authentication.AuthenticationResult;

public interface Authenticator {

    String getName();

    boolean canHandle(AuthenticationRequest request);

    AuthenticationResult authenticate(
            AuthenticationRequest request,
            AuthenticationContext context
    );
}