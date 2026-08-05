# Contributing

Thank you for improving Spring Boot Blog. Keep changes focused, tested, and safe to review.

## Development setup

1. Fork or clone the repository.
2. Create a branch from the latest `main`.
3. Run the application with `./mvnw spring-boot:run`.
4. Install browser-test dependencies with `npm ci` and `npx playwright install chromium`.

Use Java 21 or later and Node.js 24. Do not commit local databases, credentials, generated reports, or IDE files.

## Making a change

- Use a short branch name such as `fix/login-validation` or `feat/post-search`.
- Add a Flyway migration for schema changes. Never edit a migration that may already be deployed.
- Preserve server-side authorization even when the interface hides an action.
- Add comments only where they explain intent, security constraints, or a non-obvious tradeoff.
- Add or update tests that fail without the change.
- Keep user-facing text, source comments, commits, and documentation in English.
- Use Conventional Commit types (`feat`, `fix`, `perf`, `test`, `docs`, `ci`, `build`, or `chore`) so Release Please can determine versions and notes.

## Verification

Run the same checks used by CI:

```bash
./mvnw clean verify
npm ci
npm run test:frontend
npx playwright install chromium
npm run test:e2e
```

Container changes should also be verified with:

```bash
docker compose build
docker compose up
```

Run both SQLite scripts against disposable data when changing migrations, persistence, or recovery tooling. Changes to `scripts/*-sqlite.sh`, migrations, or the recovery workflow automatically trigger the backup-and-restore drill.

## Pull requests

Use a clear title following the existing `type: summary` convention. Explain the problem, the solution, security or migration impact, and the commands used to verify the change. Keep unrelated changes in separate pull requests.

The protected `main` branch requires all build, browser, dependency, CodeQL, container, and secret-scanning checks to pass. Resolve review conversations before merging. Use squash merge so each pull request produces one focused commit.

Document user-visible behavior in `README.md`, operational behavior in `docs/OPERATIONS.md`, unreleased changes in `CHANGELOG.md`, and maintainer-only release steps in `RELEASING.md`.

## Reporting security issues

Do not open a public issue for a suspected vulnerability. Follow [SECURITY.md](SECURITY.md) instead.
