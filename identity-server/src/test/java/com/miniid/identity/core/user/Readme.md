# MiniID User Management

The User Management module manages user identities and account state inside MiniID.

User identity is intentionally separated from authentication credentials. A `User` represents who the user is, while password credentials are managed by the credential subsystem.

## Architecture

UserService
    |
    +-- UserStore
    |      |
    |      +-- JpaUserStore
    |              |
    |              +-- UserRepository
    |
    +-- PasswordCredentialService

## User Entity

The `User` entity currently contains:

- User ID
- Username
- Email
- Enabled state
- Account locked state
- Creation timestamp
- Update timestamp

Password hashes are not stored in the `User` entity.

Credentials are managed separately through the credential subsystem.

## UserStore

`UserStore` is the identity-layer abstraction used to access users.

The current implementation is:

UserStore
    |
    +-- JpaUserStore
            |
            +-- UserRepository
                    |
                    +-- PostgreSQL

This prevents higher-level identity components from depending directly on JPA.

Future user-store implementations could use other storage systems without changing the higher-level user-management API.

## UserService

`UserService` coordinates user-management operations.

Current operations include:

- Create a user
- Find a user by ID
- Find a user by username
- Enable a user
- Disable a user
- Lock a user
- Unlock a user

## User Creation

Creating a user requires both an identity and an initial password credential.

The flow is:

Create User Request
        |
        v
UserService
        |
        +-- Validate input
        |
        +-- Check username uniqueness
        |
        +-- Check email uniqueness
        |
        +-- Create User
        |
        +-- Save through UserStore
        |
        +-- Create password through
        |   PasswordCredentialService
        |
        v
User Created

User creation is transactional.

If user persistence succeeds but password credential creation fails, the transaction is rolled back so MiniID does not leave a partially created account.

## Account State

Account state changes are expressed through domain operations on `User`.

Examples:

user.enable()
user.disable()
user.lock()
user.unlock()

`UserService` coordinates finding the user, applying the domain operation, and persisting the result.

For example:

disableUser(userId)
        |
        v
Find User
        |
        v
user.disable()
        |
        v
UserStore.save(user)

## Authentication Relationship

User Management and Authentication have separate responsibilities.

User Management:

Create and manage identities.

Authentication:

Verify that someone can prove ownership of an identity.

The relationship is:

BasicAuthenticator
        |
        v
UserStore
        |
        v
User
        |
        +-- enabled?
        |
        +-- locked?
        |
        v
PasswordCredentialService
        |
        v
Password verification

## Current Test Coverage

User management tests cover:

- User creation with an initial password credential
- Duplicate username rejection
- Duplicate email rejection
- Missing username validation
- Missing email validation
- Missing password validation
- Lookup by user ID
- Lookup by username
- Enable user
- Disable user
- Lock user
- Unlock user
- Missing-user handling

## Future Improvements

Planned improvements include:

- Dedicated user-management exceptions
- REST API integration
- Stronger username/email validation
- Password policy enforcement
- User profile and claims
- Roles and permissions
- Pagination and user search
- Integration tests for transaction rollback
- Audit events for user-management operations