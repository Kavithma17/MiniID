# MiniID Application Management

The Application Management module manages client applications registered with MiniID.

Applications will later be used by the OAuth 2.0 and OpenID Connect layers to identify clients and validate authorization requests.

## Architecture

```text
ApplicationService
        |
        +-- ApplicationStore
        |       |
        |       +-- JpaApplicationStore
        |               |
        |               +-- ApplicationRepository
        |
        +-- ApplicationRedirectUriStore
                |
                +-- JpaApplicationRedirectUriStore
                        |
                        +-- ApplicationRedirectUriRepository

                        
Application

An Application currently contains:
- Application ID
- Application name
- Generated client ID
- Enabled/disabled state
- Creation timestamp
- Update timestamp
The client ID is generated using SecureRandom and URL-safe Base64 encoding.
A client ID identifies an application but is not a secret.

Redirect URIs

Each application can register multiple redirect URIs.
Redirect URIs are stored separately using ApplicationRedirectUri.
Application
     |
     +-- Redirect URI
     +-- Redirect URI
     +-- Redirect URI

MiniID currently validates that redirect URIs:

- Are not blank
- Are absolute URIs
- Use HTTP or HTTPS
- Contain a host
- Do not contain URI fragments
- Do not exceed the configured storage length

Redirect URI Security
Redirect URI registration uses exact values.
MiniID does not perform wildcard, prefix, or partial matching.
For example:
Registered:
https://example.com/callback

Accepted:
https://example.com/callback

Not accepted:
https://example.com/callback/evil

This behavior will later be used by the OAuth 2.0 authorization endpoint to validate authorization requests.
Current Operations
ApplicationService currently supports:
- Create application
- Find application by ID
- Find application by client ID
- Enable application
- Disable application
- Add redirect URI
- Remove redirect URI
- List registered redirect URIs
- Check whether a redirect URI is registered