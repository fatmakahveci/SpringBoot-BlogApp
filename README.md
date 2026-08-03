# Spring Boot Blog Application

[![Java CI](https://github.com/fatmakahveci/SpringBoot-BlogApp/actions/workflows/maven.yml/badge.svg)](https://github.com/fatmakahveci/SpringBoot-BlogApp/actions/workflows/maven.yml)
[![CodeQL](https://github.com/fatmakahveci/SpringBoot-BlogApp/actions/workflows/codeql.yml/badge.svg)](https://github.com/fatmakahveci/SpringBoot-BlogApp/actions/workflows/codeql.yml)
[![Java](https://img.shields.io/badge/Java-21-orange.svg)](https://adoptium.net/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-4.1-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](LICENSE.md)

A secure, server-rendered blog platform built with Spring Boot, Thymeleaf, Spring Security, and SQLite. The application combines an accessible web interface with a documented, read-only JSON API and includes production-oriented testing, observability, containerization, and CI security controls.

## Highlights

- Draft and published post workflow with stable, slug-based URLs
- Tag management, full-text title search, sorting, and pagination
- Self-service author registration and role-based authorization
- Responsive, accessible Thymeleaf interface with explicit empty states
- Consistent HTML and JSON responses for validation and application errors
- OpenAPI documentation and Swagger UI for the JSON API
- Versioned SQLite schema management with Flyway
- Actuator health probes and structured ECS production logs
- Multi-stage, non-root Docker image with read-only runtime support
- Automated tests, coverage enforcement, static analysis, and security scanning

## Application preview

> The interface has recently been redesigned. A refreshed walkthrough will be added after the new desktop and mobile screenshots are captured.

The main workflow is straightforward:

1. Browse, search, sort, and filter published posts without signing in.
2. Open a post through its permanent public URL.
3. Register or sign in as an author to create drafts, publish content, and assign tags.
4. Sign in as an administrator to manage all content, including deletions.

## Technology stack

| Area | Technology |
|---|---|
| Runtime | Java 21, Spring Boot 4.1 |
| Web | Spring MVC, Thymeleaf, Bootstrap 5 |
| Security | Spring Security, CSRF protection, security headers, role-based access |
| Persistence | Spring Data JPA, SQLite, Flyway |
| API documentation | springdoc-openapi, Swagger UI |
| Observability | Spring Boot Actuator, health probes, ECS JSON logs |
| Quality | JUnit 6, MockMvc, JaCoCo, SpotBugs, Spotless |
| Delivery | Maven Wrapper, Docker, GitHub Actions, CodeQL, Trivy, Dependabot |

## Quick start

### Requirements

- Java 21
- Git

Maven does not need to be installed; the repository includes Maven Wrapper.

```bash
git clone https://github.com/fatmakahveci/SpringBoot-BlogApp.git
cd SpringBoot-BlogApp
./mvnw spring-boot:run
```

Open [http://localhost:8080](http://localhost:8080).

On Windows, run `mvnw.cmd spring-boot:run` instead.

When development passwords are not provided, the application generates temporary administrator and author passwords and writes them once to the startup log. For predictable local credentials, start the application with explicit values:

```bash
BLOG_ADMIN_PASSWORD='replace-with-a-strong-password' \
BLOG_AUTHOR_PASSWORD='replace-with-a-different-password' \
./mvnw spring-boot:run
```

## Roles and permissions

| Capability | Public | Author | Administrator |
|---|:---:|:---:|:---:|
| Browse published posts | Yes | Yes | Yes |
| Register and sign in | Yes | Yes | Yes |
| View drafts | No | Yes | Yes |
| Create and edit content | No | Yes | Yes |
| Delete posts and tags | No | No | Yes |

## Configuration profiles

| Profile | Purpose | Notable behavior |
|---|---|---|
| `dev` | Local development; selected by default | Uncached templates and generated temporary passwords |
| `test` | Automated test execution | Isolated in-memory SQLite database and deterministic credentials |
| `prod` | Container and production deployments | Required secrets, secure cookies, structured logs, cached templates |

The production profile requires `BLOG_DATABASE_PATH`, `BLOG_ADMIN_PASSWORD`, and `BLOG_AUTHOR_PASSWORD`. It fails fast when required account secrets are missing.

### Environment variables

| Variable | Development default | Description |
|---|---|---|
| `SPRING_PROFILES_ACTIVE` | `dev` | Active runtime profile |
| `BLOG_DATABASE_PATH` | `sample.db` | SQLite database path; required in production |
| `BLOG_ADMIN_USERNAME` | `admin` | Initial administrator username |
| `BLOG_ADMIN_PASSWORD` | Generated | Initial administrator password; required in production |
| `BLOG_AUTHOR_USERNAME` | `author` | Initial author username |
| `BLOG_AUTHOR_PASSWORD` | Generated | Initial author password; required in production |
| `SPRINGDOC_ENABLED` | `false` in production | Enables Swagger UI and OpenAPI endpoints in production |
| `BLOG_BASE_URL` | `http://localhost:8080` | Public HTTPS origin used for canonical URLs and the sitemap; required in production |

Flyway applies migrations from `src/main/resources/db/migration`. Add a new migration for each schema change; never modify a migration that has already been deployed.

## API and operational endpoints

After starting the application locally:

| Endpoint | Purpose |
|---|---|
| [Swagger UI](http://localhost:8080/swagger-ui.html) | Interactive API documentation |
| [OpenAPI JSON](http://localhost:8080/v3/api-docs/blog-api) | Machine-readable API specification |
| [Health](http://localhost:8080/actuator/health) | Sanitized application health and probe groups |
| [Application](http://localhost:8080) | Server-rendered blog interface |
| [Sitemap](http://localhost:8080/sitemap.xml) | Canonical URLs for published posts and public topics |
| [Robots](http://localhost:8080/robots.txt) | Search crawler policy and sitemap discovery |

Only the Actuator `health` and `info` endpoints are exposed over HTTP. Health component details and sensitive management endpoints remain unavailable to public clients.

### JSON API examples

```bash
curl "http://localhost:8080/api/posts?page=0&size=10&sort=newest"
curl "http://localhost:8080/api/posts?query=spring&sort=titleAsc"
curl http://localhost:8080/api/tags
```

Anonymous API clients only receive published posts. Authenticated authors and administrators may also retrieve drafts.

API failures use a consistent representation for `400 Bad Request`, `404 Not Found`, and `409 Conflict` responses:

```json
{
  "timestamp": "2026-08-02T21:00:00Z",
  "status": 404,
  "error": "Not Found",
  "message": "Could not find post missing",
  "path": "/api/posts/missing"
}
```

Equivalent browser failures render an accessible HTML error page.

## Docker

The multi-stage Dockerfile compiles the application and produces a minimal, non-root runtime image:

```bash
docker build -t springboot-blog .

docker run --rm \
  -p 8080:8080 \
  --read-only \
  --tmpfs /tmp:rw,noexec,nosuid \
  -v blog-data:/data \
  -e BLOG_BASE_URL='https://blog.example.com' \
  -e BLOG_ADMIN_PASSWORD='replace-with-a-strong-password' \
  -e BLOG_AUTHOR_PASSWORD='replace-with-a-different-password' \
  springboot-blog
```

SQLite data is stored in the `blog-data` volume. The container runs with the `prod` profile, uses an unprivileged account, writes temporary files only to `/tmp`, and reports health through `/actuator/health`.

## Testing and code quality

Run the complete verification pipeline:

```bash
./mvnw clean verify
```

This command runs:

- Unit, MVC, security, repository, migration, and real SQLite integration tests
- Spotless source formatting checks
- SpotBugs static analysis
- JaCoCo coverage reporting and the configured coverage threshold
- Spring Boot executable JAR packaging

The coverage report is generated at `target/site/jacoco/index.html`. Tests use isolated in-memory SQLite databases and never modify the repository's `sample.db` file.

## Architecture

```text
Browser ──> MVC controllers ──> Thymeleaf views
Client  ──> REST controllers ─> JSON responses
                 │
                 v
              Services ──> Repositories ──> SQLite
                              │
                              └────────────> Flyway migrations
```

- Controllers handle transport concerns and delegate business behavior.
- Services define transactional boundaries and enforce application rules.
- Entities maintain both sides of bidirectional relationships safely.
- Repositories provide persistence without leaking database access into views.
- A global exception handler produces consistent HTML and JSON failures.

## Security and CI

The application and repository include:

- Password hashing through Spring Security's delegating password encoder
- CSRF protection, secure session settings, CSP, referrer, permissions, and frame restrictions
- Role-based write and delete authorization
- Validation and database constraints for duplicate and malformed data
- GitHub dependency review for pull requests
- CodeQL static security analysis
- Trivy container vulnerability scanning
- Weekly Dependabot updates for Maven, GitHub Actions, and Docker

## Contributing

1. Create a focused branch.
2. Add or update tests for behavioral changes.
3. Run `./mvnw clean verify`.
4. Add a new Flyway migration for schema changes.
5. Open a pull request with a concise description and verification notes.

## License

Licensed under the [Apache License 2.0](LICENSE.md).
