# Security Policy

## Supported versions

Security fixes are provided for the latest release and the current `main` branch.

| Version | Supported |
|---|:---:|
| Latest release | Yes |
| `main` | Yes |
| Older releases | No |

## Reporting a vulnerability

Use GitHub's private vulnerability reporting feature on the repository's **Security** tab. Include:

- affected endpoint, component, and version;
- reproduction steps or a minimal proof of concept;
- expected and observed behavior;
- potential confidentiality, integrity, or availability impact;
- any suggested mitigation.

Do not include secrets, personal data, or active production credentials. Do not open a public issue until a fix is available.

## Response process

The maintainer will acknowledge a complete report when it is reviewed, validate its severity, prepare a fix on a private branch where appropriate, and coordinate disclosure through a GitHub security advisory. Release timing depends on impact and the availability of a safe fix.

Reporters receive updates through the private advisory. Public disclosure occurs after supported users have a reasonable opportunity to update. If the report is out of scope or cannot be reproduced, the maintainer will close it with an explanation.

## Security expectations

- Secrets must come from environment variables or GitHub encrypted secrets.
- Production runs as a non-root user with a read-only root filesystem.
- Authentication and authorization changes require negative tests.
- Administrator authentication requires TOTP MFA, and successful state-changing administrator requests are audit logged without request bodies or secrets.
- Login, registration, anonymous, author, administrator, and trusted-scanner request limits must remain independently tested.
- New dependencies must pass dependency review and vulnerability scanning.
- Security controls must not expose health details, stack traces, credentials, or private drafts.
- Backups contain production data: encrypt them, restrict access, test restoration, and dispose of expired copies securely.

Automated checks reduce risk but do not guarantee that the application is vulnerability-free.
