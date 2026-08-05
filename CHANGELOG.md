# Changelog

All notable changes to this project are documented here. The format follows [Keep a Changelog](https://keepachangelog.com/en/1.1.0/), and releases use [Semantic Versioning](https://semver.org/).

## [Unreleased]

### Added

- Professional contribution, security, and release documentation.
- Administrator TOTP MFA and append-only administrator audit events.
- Role-aware request limiting with separate authentication and trusted-scanner policies.
- Optional Redis sitemap caching with deterministic invalidation.
- Scheduled SQLite backup and restore drills with integrity verification.
- Dedicated staging configuration and an operations runbook.

### Changed

- Main branch governance now requires build, browser, dependency, CodeQL, container, and secret-scanning checks.
- Post-topic reads use bounded lazy loading and indexed query paths.
- Structured request completion logs and Sentry context include correlation, version, and environment fields.
- Package publishing now checks out an immutable stable tag and verifies its POM version before deployment.
- Redundant JDBC starter wiring and Redis repository discovery were removed.

## [1.0.0] - 2026-08-04

### Added

- Secure Spring Boot and Thymeleaf blog with SQLite persistence and Flyway migrations.
- Registration, authentication, role-based authoring, post lifecycle, and topic management.
- Read-only JSON API with OpenAPI and Swagger UI documentation.
- Responsive interface, SEO endpoints, Actuator probes, Docker support, and automated tests.
- JaCoCo, Spotless, SpotBugs, CodeQL, dependency review, Trivy, and Dependabot automation.

[Unreleased]: https://github.com/fatmakahveci/SpringBoot-BlogApp/compare/v1.0.0...HEAD
[1.0.0]: https://github.com/fatmakahveci/SpringBoot-BlogApp/releases/tag/v1.0.0
