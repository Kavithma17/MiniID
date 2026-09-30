package com.miniid.identity.core.credential.password;

public interface PasswordHasher {

    String hash(char[] password);

    boolean verify(char[] password, String encodedHash);

}