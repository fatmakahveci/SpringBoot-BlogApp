# Release Process

Releases are created from the protected `main` branch after every required check succeeds.

## Versioning

Use Semantic Versioning:

- `PATCH` for compatible fixes and security patches;
- `MINOR` for compatible features;
- `MAJOR` for incompatible behavior or data contracts.

## Release checklist

1. Confirm `main` is green and has no unresolved security alerts.
2. Review the `Unreleased` section in `CHANGELOG.md`.
3. Verify migrations against a restored copy of representative data.
4. Run `./mvnw clean verify`, frontend tests, E2E tests, and the container security workflow.
5. Create a signed `vMAJOR.MINOR.PATCH` tag through the release workflow.
6. Publish GitHub release notes and the Maven package.
7. Deploy to staging, verify readiness and smoke tests, then approve production.
8. Confirm production health, logs, rollback metadata, and the published package.

Release Please maintains the release pull request from conventional commits. Merging that pull request creates the GitHub release, which publishes the Maven package and an immutable GHCR image. The deployment workflow verifies that exact image in staging before entering the protected `production` environment.

Configure `STAGING_URL` and `PRODUCTION_URL` as environment variables. Configure `STAGING_DEPLOY_WEBHOOK` and `PRODUCTION_DEPLOY_WEBHOOK` as environment secrets when the hosting platform supports deployment webhooks. Production approvals are managed through the GitHub `production` environment.

## Rollback

Redeploy the previously verified immutable image. Database migrations must be forward-compatible; restore from a tested backup only when a forward fix cannot safely recover the data. Record the incident and corrective action before the next release.

## Security releases

Coordinate embargoed fixes through a private GitHub security advisory. Publish the patched release, advisory, package, and container as one coordinated operation. Never disclose credentials or exploit details that put unpatched users at immediate risk.
