# Changelog

All notable changes to this project are documented here. The format follows [Keep a Changelog](https://keepachangelog.com/en/1.1.0/), and releases use [Semantic Versioning](https://semver.org/).

## [1.2.0](https://github.com/fatmakahveci/SpringBoot-BlogApp/compare/v1.1.0...v1.2.0) (2026-08-05)


### Features

* add development sample posts and topics ([#52](https://github.com/fatmakahveci/SpringBoot-BlogApp/issues/52)) ([85a3897](https://github.com/fatmakahveci/SpringBoot-BlogApp/commit/85a3897029559d91ffad782dcb634754148e00f7))
* replace technical samples with everyday stories ([#55](https://github.com/fatmakahveci/SpringBoot-BlogApp/issues/55)) ([70b76f5](https://github.com/fatmakahveci/SpringBoot-BlogApp/commit/70b76f57dc2f0a17fbf3c6988655984032e50e5a))
* **ui:** align blog experience with everyday stories ([#57](https://github.com/fatmakahveci/SpringBoot-BlogApp/issues/57)) ([41f5501](https://github.com/fatmakahveci/SpringBoot-BlogApp/commit/41f550181264c64bf8a170aa28eff1f4482c00c0))


### Bug Fixes

* **dev:** remove obsolete demo content ([#56](https://github.com/fatmakahveci/SpringBoot-BlogApp/issues/56)) ([344059a](https://github.com/fatmakahveci/SpringBoot-BlogApp/commit/344059a355872ae70d44b7d8ec727b11a36e98dc))
* seed development content atomically ([#54](https://github.com/fatmakahveci/SpringBoot-BlogApp/issues/54)) ([0afabbf](https://github.com/fatmakahveci/SpringBoot-BlogApp/commit/0afabbfa415baee4c6f183d9f647a7f75dfefdee))

## [1.1.0](https://github.com/fatmakahveci/SpringBoot-BlogApp/compare/v1.0.0...v1.1.0) (2026-08-05)


### Features

* add administrator audit logging ([#38](https://github.com/fatmakahveci/SpringBoot-BlogApp/issues/38)) ([831cd34](https://github.com/fatmakahveci/SpringBoot-BlogApp/commit/831cd34484cdb7416d6e645b175dbebb3e0a1c25))
* add branded browser favicon ([#47](https://github.com/fatmakahveci/SpringBoot-BlogApp/issues/47)) ([9ebe37e](https://github.com/fatmakahveci/SpringBoot-BlogApp/commit/9ebe37efa839eacfd70a75381e811d045a9e04b1))
* add correlated request observability ([#34](https://github.com/fatmakahveci/SpringBoot-BlogApp/issues/34)) ([1256794](https://github.com/fatmakahveci/SpringBoot-BlogApp/commit/125679433c5d07071fbe813d1498df080347bd5f))
* add explicit topic management actions ([#35](https://github.com/fatmakahveci/SpringBoot-BlogApp/issues/35)) ([d5d0291](https://github.com/fatmakahveci/SpringBoot-BlogApp/commit/d5d0291bea635def80a4780f295df24c0dd9dc76))
* add predictable optional Redis caching ([#40](https://github.com/fatmakahveci/SpringBoot-BlogApp/issues/40)) ([6a051d1](https://github.com/fatmakahveci/SpringBoot-BlogApp/commit/6a051d1d63649dea1dca41a5ec84d22204604c43))
* add secure topic editing ([#25](https://github.com/fatmakahveci/SpringBoot-BlogApp/issues/25)) ([1232828](https://github.com/fatmakahveci/SpringBoot-BlogApp/commit/1232828bde9f12325721da72b2052c4dd85d9918))
* enforce role-aware request limits ([#39](https://github.com/fatmakahveci/SpringBoot-BlogApp/issues/39)) ([dfbc3f7](https://github.com/fatmakahveci/SpringBoot-BlogApp/commit/dfbc3f76cdfb7617174cc9a6d0ecb526e2b04cb3))
* enrich request observability context ([#44](https://github.com/fatmakahveci/SpringBoot-BlogApp/issues/44)) ([92e885a](https://github.com/fatmakahveci/SpringBoot-BlogApp/commit/92e885ac92375d45003aad0e158d99b8bf2a6d6c))
* refine colors and typography ([#26](https://github.com/fatmakahveci/SpringBoot-BlogApp/issues/26)) ([bdad2fb](https://github.com/fatmakahveci/SpringBoot-BlogApp/commit/bdad2fb90cca63e591fca004db494f852e011391))
* require TOTP MFA for administrators ([#37](https://github.com/fatmakahveci/SpringBoot-BlogApp/issues/37)) ([e5b03da](https://github.com/fatmakahveci/SpringBoot-BlogApp/commit/e5b03da62f75898d38fb465df08f56a6de6ebb25))
* **ui:** complete branding assets and refresh demo ([#48](https://github.com/fatmakahveci/SpringBoot-BlogApp/issues/48)) ([6d61dac](https://github.com/fatmakahveci/SpringBoot-BlogApp/commit/6d61dac7e17328e8e16e4b59dca78981abd2db1f))


### Bug Fixes

* make browse posts navigation visible ([#27](https://github.com/fatmakahveci/SpringBoot-BlogApp/issues/27)) ([de3542d](https://github.com/fatmakahveci/SpringBoot-BlogApp/commit/de3542df0530ba46b1aa7ae578de237d0f7c7ba2))
* make full topic rows clickable ([#29](https://github.com/fatmakahveci/SpringBoot-BlogApp/issues/29)) ([350f36c](https://github.com/fatmakahveci/SpringBoot-BlogApp/commit/350f36c646c510e2b2638270c58920f890725783))
* pin footer to viewport bottom ([#28](https://github.com/fatmakahveci/SpringBoot-BlogApp/issues/28)) ([6ca8a06](https://github.com/fatmakahveci/SpringBoot-BlogApp/commit/6ca8a069a94ae8ba86e78258447bfa9692856334))
* version stylesheet across all pages ([#30](https://github.com/fatmakahveci/SpringBoot-BlogApp/issues/30)) ([daaf875](https://github.com/fatmakahveci/SpringBoot-BlogApp/commit/daaf87526b1df839e3f3f3ff9d5e4defeefa3e18))


### Performance Improvements

* eliminate post tag N plus one queries ([#41](https://github.com/fatmakahveci/SpringBoot-BlogApp/issues/41)) ([67a2745](https://github.com/fatmakahveci/SpringBoot-BlogApp/commit/67a2745645e17ed1d97597d944731efa5e4ebdde))


### Documentation

* add production operations runbook ([#45](https://github.com/fatmakahveci/SpringBoot-BlogApp/issues/45)) ([ae77a3f](https://github.com/fatmakahveci/SpringBoot-BlogApp/commit/ae77a3f3a1246fb48aa834de2cc40dbaddf2114c))
* add project governance guides ([#31](https://github.com/fatmakahveci/SpringBoot-BlogApp/issues/31)) ([abc4b08](https://github.com/fatmakahveci/SpringBoot-BlogApp/commit/abc4b08edb06570b848d5de79dce91da660ec38f))

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
