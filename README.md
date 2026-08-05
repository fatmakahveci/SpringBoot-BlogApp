# Spring Boot Blog Application

[![Java CI](https://github.com/fatmakahveci/SpringBoot-BlogApp/actions/workflows/maven.yml/badge.svg)](https://github.com/fatmakahveci/SpringBoot-BlogApp/actions/workflows/maven.yml)
[![Security Dashboard](https://github.com/fatmakahveci/SpringBoot-BlogApp/actions/workflows/security.yml/badge.svg)](https://github.com/fatmakahveci/SpringBoot-BlogApp/actions/workflows/security.yml)
[![Java](https://img.shields.io/badge/Java-21-orange.svg)](https://adoptium.net/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-4.1-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![Coverage](https://img.shields.io/badge/coverage-85%25%20line%20%7C%2065%25%20branch-brightgreen.svg)](#testing)
[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](LICENSE.md)

A secure, server-rendered blog built with Spring Boot, Thymeleaf, Spring Security, and SQLite. It includes author registration, role-based publishing, topic management, a read-only JSON API, production health checks, and a hardened CI/CD pipeline.

![Spring Blog interface](demo.gif)

## Features

- Draft and published posts with stable slug URLs
- Search, sorting, pagination, and topic management
- Public, author, and administrator roles
- Administrator TOTP MFA and audit logging
- Responsive, accessible Thymeleaf interface
- OpenAPI documentation and consistent JSON errors
- Flyway migrations, Actuator probes, Redis-ready caching, and Sentry integration
- Non-root, read-only Docker runtime
- JUnit, Playwright, JaCoCo, SpotBugs, CodeQL, Trivy, and Dependabot checks

## Stack

| Area | Technology |
|---|---|
| Runtime | Java 21, Spring Boot 4.1 |
| Web | Spring MVC, Thymeleaf, Bootstrap 5 |
| Security | Spring Security, TOTP MFA, CSRF and security headers |
| Data | Spring Data JPA, SQLite, Flyway, optional Redis |
| API | springdoc-openapi 3.1, Swagger UI |
| Observability | Actuator, ECS logs, Sentry 8.51 |
| Delivery | Maven, Docker, GitHub Actions |

## Quick start

Requirements: Java 21 or later and Git. Maven is provided by the wrapper.

```bash
git clone https://github.com/fatmakahveci/SpringBoot-BlogApp.git
cd SpringBoot-BlogApp
./mvnw spring-boot:run
```

Open [http://localhost:8080](http://localhost:8080). On Windows, run `mvnw.cmd spring-boot:run`.

The default `dev` profile uses `sample.db` and prints one-time temporary administrator and author passwords at startup. To choose predictable local credentials:

```bash
BLOG_ADMIN_PASSWORD='replace-with-a-strong-password' \
BLOG_AUTHOR_PASSWORD='replace-with-a-different-password' \
./mvnw spring-boot:run
```

## Roles

| Capability | Public | Author | Admin |
|---|:---:|:---:|:---:|
| Browse published posts | ✓ | ✓ | ✓ |
| Register and sign in | ✓ | ✓ | ✓ |
| View drafts and edit content | — | ✓ | ✓ |
| Delete posts and topics | — | — | ✓ |

Administrators must complete TOTP verification after password authentication.

## Configuration

Profiles are separated by environment:

| Profile | Purpose |
|---|---|
| `dev` | Local development; default |
| `test`, `e2e` | Isolated automated tests |
| `staging` | Pre-production with independent data and secrets |
| `prod` | Hardened production settings |
| `redis` | Optional shared cache; combine with another profile |

Important production variables:

| Variable | Purpose |
|---|---|
| `BLOG_DATABASE_PATH` | SQLite database path |
| `BLOG_ADMIN_PASSWORD`, `BLOG_AUTHOR_PASSWORD` | Initial account credentials |
| `BLOG_MFA_KEY`, `BLOG_MFA_ENCRYPTION_SALT` | TOTP secret encryption |
| `BLOG_BASE_URL` | Public HTTPS origin for canonical URLs |
| `SENTRY_DSN`, `SENTRY_ENVIRONMENT` | Optional error reporting |
| `REDIS_HOST`, `REDIS_PORT`, `REDIS_PASSWORD`, `REDIS_SSL` | Optional Redis connection |

Production and staging fail fast when required secrets are missing. Never share databases or secrets between environments. See [Operations](docs/OPERATIONS.md) for deployment details.

## API and health

| Endpoint | Purpose |
|---|---|
| [`/swagger-ui.html`](http://localhost:8080/swagger-ui.html) | Interactive API documentation |
| [`/v3/api-docs/blog-api`](http://localhost:8080/v3/api-docs/blog-api) | OpenAPI JSON |
| [`/api/posts`](http://localhost:8080/api/posts) | Published posts |
| [`/api/tags`](http://localhost:8080/api/tags) | Topics |
| [`/actuator/health/readiness`](http://localhost:8080/actuator/health/readiness) | Traffic readiness and database health |
| [`/actuator/health/liveness`](http://localhost:8080/actuator/health/liveness) | Process health |
| [`/sitemap.xml`](http://localhost:8080/sitemap.xml) | Published canonical URLs |

Anonymous API clients only receive published posts. Errors use one JSON structure for `400`, `404`, and `409` responses. Every response includes an `X-Request-ID` for log and Sentry correlation.

## Docker

```bash
export BLOG_ADMIN_PASSWORD='replace-with-a-strong-password'
export BLOG_AUTHOR_PASSWORD='replace-with-a-different-password'
export BLOG_MFA_KEY='replace-with-a-high-entropy-key'
export BLOG_MFA_ENCRYPTION_SALT='replace-with-random-hex'
docker compose up --build
```

Compose runs the application as UID/GID `10001` with a read-only root filesystem, dropped capabilities, `no-new-privileges`, bounded resources, and separate readiness/liveness checks. SQLite data is stored in the `blog-data` volume.

## Testing

```bash
./mvnw clean verify
npm ci
npm run test:frontend
npx playwright install chromium
npm run test:e2e
```

Maven runs unit and integration tests, Spotless, SpotBugs, packaging, and JaCoCo. Coverage thresholds are 85% for lines/instructions and 65% for branches; Vitest enforces frontend thresholds. Test databases are isolated from `sample.db`.

## Architecture

```text
Browser ──> MVC controllers ──> Thymeleaf
Client  ──> REST controllers ─> JSON
                 │
              Services ──> Repositories ──> SQLite
                              └────────────> Flyway
```

Controllers handle transport, services own transactions and application rules, repositories isolate persistence, and the global exception handler keeps HTML and JSON failures consistent.

## Security and operations

The repository requires build, frontend/E2E, dependency, CodeQL, container, and GitGuardian checks before merge. Weekly Dependabot updates cover Maven, npm, GitHub Actions, and Docker. Backup and restore are exercised by the disaster-recovery workflow.

- Report vulnerabilities privately: [SECURITY.md](SECURITY.md)
- Development workflow: [CONTRIBUTING.md](CONTRIBUTING.md)
- Operations and recovery: [docs/OPERATIONS.md](docs/OPERATIONS.md)
- Releases: [CHANGELOG.md](CHANGELOG.md) and [RELEASING.md](RELEASING.md)

## License

Licensed under the [Apache License 2.0](LICENSE.md).
