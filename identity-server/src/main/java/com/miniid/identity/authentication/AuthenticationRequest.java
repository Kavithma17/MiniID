package com.miniid.identity.authentication;

import java.util.Arrays;

public class AuthenticationRequest {

    private final String username;
    private final char[] password;

    public AuthenticationRequest(String username, char[] password) {
        this.username = username;
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public char[] getPassword() {
        return password;
    }

    public void clearCredentials() {
        if (password != null) {
            Arrays.fill(password, '\0');
        }
    }
}