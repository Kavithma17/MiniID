PasswordCredential
        → represents stored password credential

PasswordCredentialRepository
        → reads/writes credentials

PasswordHasher
        → hashing contract

PBKDF2PasswordHasher
        → PBKDF2 implementation

PasswordCredentialService
        → coordinates credential operations


                PasswordCredentialService
                    │
          ┌─────────┴─────────┐
          ▼                   ▼
PasswordCredential       PasswordHasher
   Repository                  │
      │                        ▼
      ▼                PBKDF2PasswordHasher
PasswordCredential
      │
      ▼
 PostgreSQL