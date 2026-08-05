# Operations Runbook

This runbook covers the operational controls shipped with Spring Boot Blog. Deployment-specific owners must define their recovery point objective (RPO), recovery time objective (RTO), retention period, and off-site storage policy before production launch.

## Runtime environments

Use one primary profile and add `redis` only when a managed Redis service is available:

| Environment | Profiles | Data and secrets |
|---|---|---|
| Development | `dev` | Local `sample.db`; generated credentials are allowed |
| Automated tests | `test` or `e2e` | Isolated in-memory SQLite and deterministic test credentials |
| Staging | `staging` or `staging,redis` | Dedicated database, origin, credentials, MFA keys, and Sentry environment |
| Production | `prod` or `prod,redis` | Production-only database and secrets supplied by the deployment platform |

Never reuse a database, MFA encryption key, session context, Redis namespace, webhook, or Sentry environment between staging and production.

## Health and traffic

- `/actuator/health/liveness` answers whether the process should be restarted. It intentionally has no database dependency.
- `/actuator/health/readiness` answers whether the instance should receive traffic and includes SQLite connectivity.
- `/actuator/health` is useful for local diagnosis but does not expose component details publicly.

Remove an instance from traffic when readiness fails. Restart it only when liveness fails or investigation identifies a process-level fault.

## Backup procedure

Run backups against the mounted SQLite database; do not copy a live database with a plain filesystem command.

```bash
scripts/backup-sqlite.sh /data/blog.db /secure-backups/blog-$(date -u +%Y%m%dT%H%M%SZ).db
```

The command uses SQLite's online backup operation, validates the result with `PRAGMA integrity_check`, writes with owner-only permissions, and atomically publishes the completed file. Encrypt backups at rest and copy them to storage outside the application host.

## Restore procedure

1. Stop writes and remove the application from traffic.
2. Preserve the failed database and its logs for investigation.
3. Select a backup whose timestamp and checksum match the recovery decision.
4. Restore to a new path:

   ```bash
   scripts/restore-sqlite.sh /secure-backups/blog.db /data/blog-restored.db
   ```

5. Set `BLOG_DATABASE_PATH=/data/blog-restored.db` and start one instance.
6. Verify readiness, migration completion, authentication, and representative public content.
7. Re-enable traffic, monitor error rate and latency, and record actual data loss and recovery time.

Never overwrite the only copy of a failed database. The scheduled Disaster Recovery workflow exercises these steps against disposable data and verifies the restored record through the public API.

## Incident triage

Start with the response `X-Request-ID`. Search structured logs using `request.id`, then correlate `user.name`, `service.version`, `service.environment`, status, and duration. The same request, user, version, and environment context is attached to Sentry when configured.

Do not paste credentials, MFA secrets, database files, session cookies, or personal data into issues or chat. Follow `SECURITY.md` for suspected vulnerabilities.

## Security maintenance

Review the GitHub Security tab and the scheduled Security Dashboard every week. Treat open Dependabot, CodeQL, secret-scanning, and Trivy findings as release blockers until they are fixed or documented as reviewed false positives. Validate dependency updates with the full Maven, frontend, and E2E suites; never update Spring Boot-managed transitive libraries as an untested group.

After a base-image or runtime dependency update, verify the container starts as UID/GID `10001`, remains functional with a read-only root filesystem and dropped capabilities, and reports separate liveness and readiness states. Publish production images by immutable digest and retain the scan result with the release evidence.

## Release rollback

Redeploy the previously verified image by immutable digest. Prefer a forward database fix because Flyway migrations are not automatically reversible. Restore a tested backup only after evaluating data written since that backup and recording the recovery decision.
