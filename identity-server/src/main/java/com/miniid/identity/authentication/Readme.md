# MiniID Authentication Framework

The Authentication Framework is responsible for coordinating how a user proves their identity inside MiniID.

It is intentionally separated from protocols such as OAuth 2.0 and OpenID Connect.

OAuth/OIDC will use this framework to authenticate users, but the authentication framework itself does not depend on those protocols.

---

## Architecture

The current authentication path is:

AuthenticationRequest
        |
        v
AuthenticationService
        |
        v
AuthenticationFlow
        |
        v
Authenticator
        |
        v
BasicAuthenticator
        |
        +-----------------------+
        |                       |
        v                       v
    UserStore       PasswordCredentialService
        |                       |
        v                       v
      User            PasswordCredential
                                |
                                v
                         PasswordHasher
                                |
                                v
                     PBKDF2PasswordHasher

---

## Main Components

### AuthenticationRequest

`AuthenticationRequest` contains the credentials supplied for an authentication attempt.

Currently it contains:

- username
- password

Passwords are stored as `char[]` rather than `String` so the credential can be cleared from memory after authentication.

The request creates defensive copies of password arrays to avoid exposing its internal credential state.

After the authentication operation finishes, the credentials are cleared.

---

### AuthenticationContext

`AuthenticationContext` represents state associated with an authentication attempt.

Currently it stores:

- context ID
- authenticated user ID
- username
- additional properties

The context is created by `AuthenticationService` and passed through the authentication process.

Plaintext passwords should never be stored inside the context.

In the future, this context can carry information required by multi-step authentication flows such as MFA.

---

### AuthenticationResult

`AuthenticationResult` represents the outcome of authentication.

Possible states are:

- `SUCCESS`
- `FAILURE`
- `INCOMPLETE`

`INCOMPLETE` is reserved for authentication flows that require another step, such as TOTP-based MFA.

A successful result contains the authenticated user's ID and username.

---

### Authenticator

`Authenticator` is the contract implemented by authentication mechanisms.

An authenticator must provide:

- a name
- a way to determine whether it can handle a request
- authentication logic

This allows MiniID to support different authentication mechanisms without coupling the authentication framework to a specific implementation.

Examples:

Authenticator
    |
    +-- BasicAuthenticator
    +-- TOTPAuthenticator        [future]
    +-- PasskeyAuthenticator     [future]
    +-- FederatedAuthenticator   [future]

---

## BasicAuthenticator

`BasicAuthenticator` implements username/password authentication.

Its responsibility is to:

1. Read the username from the authentication request.
2. Resolve the user through `UserStore`.
3. Check whether the user account is enabled.
4. Check whether the user account is locked.
5. Delegate password verification to `PasswordCredentialService`.
6. Populate `AuthenticationContext` after successful authentication.
7. Return an `AuthenticationResult`.

The authenticator does not directly access:

- JPA repositories
- password hashes
- PBKDF2
- PostgreSQL

This keeps authentication logic separated from persistence and credential implementation details.

### Successful Authentication

Username + Password
        |
        v
BasicAuthenticator
        |
        v
UserStore.findByUsername()
        |
        v
      User
        |
        +-- enabled?
        |
        +-- locked?
        |
        v
PasswordCredentialService.verifyPassword()
        |
        v
PasswordCredential
        |
        v
PasswordHasher.verify()
        |
        v
PBKDF2 verification
        |
        v
AuthenticationContext updated
        |
        v
SUCCESS

### Failed Authentication

An unknown username and an incorrect password both produce a generic authentication failure.

This avoids unnecessarily exposing whether a particular username exists.

Disabled and locked accounts are also rejected before password authentication succeeds.

---

## AuthenticationFlow

`AuthenticationFlow` describes which authenticators participate in an authentication process.

Currently MiniID uses a simple flow containing the basic authenticator.

Example:

AuthenticationFlow
        |
        v
[ BasicAuthenticator ]

The flow is represented separately from the execution logic so that different applications can eventually use different authentication requirements.

Future examples:

Application A

[ BasicAuthenticator ]

Application B

[ BasicAuthenticator -> TOTPAuthenticator ]

Application C

[ PasskeyAuthenticator ]

---

## AuthenticationService

`AuthenticationService` executes an `AuthenticationFlow`.

Its responsibilities are:

1. Create an `AuthenticationContext`.
2. Iterate through the authenticators configured in the flow.
3. Check whether each authenticator can handle the request.
4. Execute applicable authenticators.
5. Stop and return when authentication fails.
6. Return the authenticated identity after successful processing.
7. Clear request credentials when authentication finishes.

Credential cleanup is performed in a `finally` block so passwords are cleared even when authentication fails.

---

## Separation From OAuth 2.0 / OpenID Connect

OAuth 2.0 and OpenID Connect are not authenticators.

They are protocol layers that will sit above the authentication framework.

The future flow will look like:

Application
     |
     v
GET /oauth2/authorize
     |
     v
OAuth/OIDC Protocol Layer
     |
     v
AuthenticationService
     |
     v
AuthenticationFlow
     |
     v
BasicAuthenticator
     |
     v
Authenticated User
     |
     v
Authorization Code
     |
     v
Token Endpoint
     |
     v
Access Token / ID Token

This means protocol implementations do not need to know how passwords are verified.

They only need the result of the authentication process.

---

## Current Test Coverage

### BasicAuthenticator

Tests currently cover:

- successful username/password authentication
- unknown user
- incorrect password
- disabled user
- locked user

### AuthenticationService

Tests currently cover:

- successful authenticator execution
- authenticator failure
- requests that cannot be handled by an authenticator
- credential cleanup after authentication

---

## Future Improvements

The framework is intentionally small at this stage.

Planned extensions include:

- multi-step authentication
- TOTP authentication
- passkey/WebAuthn authentication
- federated authentication
- application-specific authentication flows
- authentication session/context persistence
- MFA challenge handling

These features should extend the existing authenticator and flow abstractions rather than introducing protocol-specific authentication logic.