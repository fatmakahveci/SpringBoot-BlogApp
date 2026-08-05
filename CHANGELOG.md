# Changelog

All notable changes to this project are documented here. The format follows [Keep a Changelog](https://keepachangelog.com/en/1.1.0/), and releases use [Semantic Versioning](https://semver.org/).

## [1.2.0](https://github.com/fatmakahveci/SpringBoot-BlogApp/compare/v1.1.0...v1.2.0) (2026-08-05)


### Features

* add administrator audit logging ([#38](https://github.com/fatmakahveci/SpringBoot-BlogApp/issues/38)) ([b0fc584](https://github.com/fatmakahveci/SpringBoot-BlogApp/commit/b0fc58425a37e12678b78c5960b44d28030e5b03))
* add branded browser favicon ([#47](https://github.com/fatmakahveci/SpringBoot-BlogApp/issues/47)) ([b25df95](https://github.com/fatmakahveci/SpringBoot-BlogApp/commit/b25df95fbf557b27ea4a5b357d4d563ab44fdda9))
* add correlated request observability ([#34](https://github.com/fatmakahveci/SpringBoot-BlogApp/issues/34)) ([2b9feb1](https://github.com/fatmakahveci/SpringBoot-BlogApp/commit/2b9feb1adbafaae7879500fc24052ab927684d84))
* add explicit topic management actions ([#35](https://github.com/fatmakahveci/SpringBoot-BlogApp/issues/35)) ([cff1f63](https://github.com/fatmakahveci/SpringBoot-BlogApp/commit/cff1f6333f1066faf3fd47aef3794fcb60366760))
* add predictable optional Redis caching ([#40](https://github.com/fatmakahveci/SpringBoot-BlogApp/issues/40)) ([2e3a616](https://github.com/fatmakahveci/SpringBoot-BlogApp/commit/2e3a616a139a0e9651a7b0028b8361ad393db26e))
* add secure topic editing ([#25](https://github.com/fatmakahveci/SpringBoot-BlogApp/issues/25)) ([694e5bf](https://github.com/fatmakahveci/SpringBoot-BlogApp/commit/694e5bfa7f5e7d50015ac9c5955765ab11ab5c42))
* enforce role-aware request limits ([#39](https://github.com/fatmakahveci/SpringBoot-BlogApp/issues/39)) ([ff5a0ab](https://github.com/fatmakahveci/SpringBoot-BlogApp/commit/ff5a0ab9de9966af21dbc1b746800be0231e68ff))
* enrich request observability context ([#44](https://github.com/fatmakahveci/SpringBoot-BlogApp/issues/44)) ([a77fc62](https://github.com/fatmakahveci/SpringBoot-BlogApp/commit/a77fc62b76342add9c4029132f591c353d146e86))
* improve search engine optimization ([6bb4e65](https://github.com/fatmakahveci/SpringBoot-BlogApp/commit/6bb4e653dea421f1342227e12792956f90f00de4))
* modernize blog security and user experience ([efb88a6](https://github.com/fatmakahveci/SpringBoot-BlogApp/commit/efb88a60e651cdfafe6d89988414c9d648ee3412))
* refine colors and typography ([#26](https://github.com/fatmakahveci/SpringBoot-BlogApp/issues/26)) ([64fc032](https://github.com/fatmakahveci/SpringBoot-BlogApp/commit/64fc032daff8870d3adfb291991589fc51cd047e))
* require TOTP MFA for administrators ([#37](https://github.com/fatmakahveci/SpringBoot-BlogApp/issues/37)) ([a49c5b3](https://github.com/fatmakahveci/SpringBoot-BlogApp/commit/a49c5b3f34c1453999edfb896da39936c012914f))
* separate production health probes ([96f2347](https://github.com/fatmakahveci/SpringBoot-BlogApp/commit/96f2347bd0e43beeec2e5141f188f2429ec75640))
* **ui:** complete branding assets and refresh demo ([#48](https://github.com/fatmakahveci/SpringBoot-BlogApp/issues/48)) ([6030414](https://github.com/fatmakahveci/SpringBoot-BlogApp/commit/6030414c132662344e6cce7508a1297f54da74c0))


### Bug Fixes

* allow isolated SQLite native execution ([fd086d2](https://github.com/fatmakahveci/SpringBoot-BlogApp/commit/fd086d29a550000bf0866273ff52d86a7145681c))
* assign hardened tmpfs ownership ([0ff1e41](https://github.com/fatmakahveci/SpringBoot-BlogApp/commit/0ff1e4194460439de991a422d3bac666d3ece9e0))
* configure hardened policy smoke test ([ca9924a](https://github.com/fatmakahveci/SpringBoot-BlogApp/commit/ca9924a5384ac451c17e1587db9734e6c76769e3))
* eliminate slug generation ReDoS risk ([3e6ff34](https://github.com/fatmakahveci/SpringBoot-BlogApp/commit/3e6ff34fa2718af4f9527996d547a2a8f4c926fb))
* enforce severity threshold for SARIF scans ([4b6b0e7](https://github.com/fatmakahveci/SpringBoot-BlogApp/commit/4b6b0e7f57b69ac812fa06be072c81b8f2bb5947))
* isolate SQLite native runtime mount ([8d1141b](https://github.com/fatmakahveci/SpringBoot-BlogApp/commit/8d1141b90f6c487ce1321c1e4e395100de2140d2))
* keep footer at viewport bottom ([#23](https://github.com/fatmakahveci/SpringBoot-BlogApp/issues/23)) ([fda7c04](https://github.com/fatmakahveci/SpringBoot-BlogApp/commit/fda7c04fa81c8e0614c720b5dcff23d79ad794f1))
* make browse posts navigation visible ([#27](https://github.com/fatmakahveci/SpringBoot-BlogApp/issues/27)) ([b7f00df](https://github.com/fatmakahveci/SpringBoot-BlogApp/commit/b7f00df09920a2061770a934603be8590e74633a))
* make full topic rows clickable ([#29](https://github.com/fatmakahveci/SpringBoot-BlogApp/issues/29)) ([05e2f6c](https://github.com/fatmakahveci/SpringBoot-BlogApp/commit/05e2f6ce31fbcf4c38a4323f9900fa0562fee186))
* patch container runtime packages ([7f7278d](https://github.com/fatmakahveci/SpringBoot-BlogApp/commit/7f7278d101858385f366259de47f9aec13d2598b))
* patch runtime image packages ([8b1ecc7](https://github.com/fatmakahveci/SpringBoot-BlogApp/commit/8b1ecc7c09e5b9755fc27833ac85b32b581bf7e7))
* pin footer to viewport bottom ([#28](https://github.com/fatmakahveci/SpringBoot-BlogApp/issues/28)) ([1d9aa4e](https://github.com/fatmakahveci/SpringBoot-BlogApp/commit/1d9aa4ecae6288b04a1511908ba56571c5305e0f))
* restore post creation and navigation ([47e7c02](https://github.com/fatmakahveci/SpringBoot-BlogApp/commit/47e7c025ce583e68a20cfff7d7007de1dc629078))
* use glibc runtime for SQLite ([bd2e345](https://github.com/fatmakahveci/SpringBoot-BlogApp/commit/bd2e3450e270e2ea92d7372b9e447f0b27772fe0))
* use valid Trivy action release ([41cb875](https://github.com/fatmakahveci/SpringBoot-BlogApp/commit/41cb87555af7553afbe286ed1e370c89f651cac6))
* version stylesheet across all pages ([#30](https://github.com/fatmakahveci/SpringBoot-BlogApp/issues/30)) ([f9b2880](https://github.com/fatmakahveci/SpringBoot-BlogApp/commit/f9b28803b1295e423270b30c5c53d77fd177e57c))


### Performance Improvements

* eliminate post tag N plus one queries ([#41](https://github.com/fatmakahveci/SpringBoot-BlogApp/issues/41)) ([2dadce1](https://github.com/fatmakahveci/SpringBoot-BlogApp/commit/2dadce1e2faa683d1b60e7ff37c9c46be26439f6))


### Documentation

* add production operations runbook ([#45](https://github.com/fatmakahveci/SpringBoot-BlogApp/issues/45)) ([59167a7](https://github.com/fatmakahveci/SpringBoot-BlogApp/commit/59167a7bd51b5e6036907fc019e5925daff72895))
* add project governance guides ([#31](https://github.com/fatmakahveci/SpringBoot-BlogApp/issues/31)) ([106f1d1](https://github.com/fatmakahveci/SpringBoot-BlogApp/commit/106f1d1a29b2cc661191552d359ae39b2c507c8b))
* refresh README and security badges ([#22](https://github.com/fatmakahveci/SpringBoot-BlogApp/issues/22)) ([19b7a1f](https://github.com/fatmakahveci/SpringBoot-BlogApp/commit/19b7a1fe539d05b343f26312f743ed1ded07affa))

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
