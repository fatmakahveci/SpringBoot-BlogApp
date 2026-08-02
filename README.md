# Spring Boot Blog Application

[![Build](https://github.com/fatmakahveci/SpringBoot-BlogApp/actions/workflows/maven.yml/badge.svg)](https://github.com/fatmakahveci/SpringBoot-BlogApp/actions/workflows/maven.yml)
[![License](https://img.shields.io/badge/license-Apache%202.0-blue.svg)](LICENSE.md)

A small server-rendered blog for creating posts, assigning tags, and browsing posts by tag. The application uses a layered Spring architecture and stores data in SQLite.

## Demo

![Blog application demo](demo.gif)

## Features

- Create, edit, and delete posts
- Create and delete tags
- Assign multiple tags to a post
- Browse posts by tag
- Search post titles, sort results, and browse paginated post lists
- Draft/published workflow, summaries, timestamps, and stable slug-based post URLs
- CSRF protection and restrictive browser security headers
- Role-based authorization for authors and administrators
- Unit and MVC integration tests

## Technology

- Java 21
- Spring Boot 4.1
- Spring MVC, Thymeleaf, Spring Data JPA, and Spring Security
- SQLite
- Flyway database migrations
- Maven Wrapper
- Bootstrap 5

## Run locally

Requirements: Java 21. Maven does not need to be installed because the repository includes Maven Wrapper.

```bash
git clone https://github.com/fatmakahveci/SpringBoot-BlogApp.git
cd SpringBoot-BlogApp
./mvnw spring-boot:run
```

Open [http://localhost:8080](http://localhost:8080).

### Accounts

Public pages do not require an account. Authors can create and update content; administrators can also delete it. Configure credentials with environment variables:

```bash
BLOG_ADMIN_USERNAME=admin \
BLOG_ADMIN_PASSWORD='replace-with-a-strong-password' \
BLOG_AUTHOR_USERNAME=author \
BLOG_AUTHOR_PASSWORD='replace-with-a-different-password' \
./mvnw spring-boot:run
```

If a password is omitted, the application generates a temporary password and prints it once in the startup log. There are no fixed default passwords.

By default, data is stored in `sample.db`. Set `BLOG_DATABASE_PATH` to use another location:

```bash
BLOG_DATABASE_PATH=/absolute/path/blog.db ./mvnw spring-boot:run
```

Flyway applies versioned scripts from `src/main/resources/db/migration`. Existing databases created before Flyway are automatically baselined at V1 and upgraded without deleting their data. Add a new migration for every future schema change; do not edit a migration that has already been applied.

## Test and build

```bash
./mvnw verify
```

The packaged application is written to `target/springboot.jar`.

Tests use an isolated in-memory SQLite database. They run the same Flyway migrations as the application but never read or modify `sample.db`.

## Run with Docker

Build the JAR and image, then start the container:

```bash
./mvnw package
docker build -t springboot-blog .
docker run --rm -p 8080:8080 -v blog-data:/data \
  -e BLOG_DATABASE_PATH=/data/blog.db \
  -e BLOG_ADMIN_PASSWORD='replace-with-a-strong-password' \
  -e BLOG_AUTHOR_PASSWORD='replace-with-a-different-password' \
  springboot-blog
```

## HTTP routes

| Method | Route | Purpose |
|---|---|---|
| `GET` | `/` | Render the blog home page |
| `GET` | `/api/posts` | Return a paginated post list as JSON |
| `GET` | `/api/posts/{slug}` | Return a published post as JSON |
| `GET` | `/posts/add` | Render the create-post form |
| `POST` | `/posts` | Create a post |
| `GET` | `/posts/{id}` | Render the edit-post form |
| `GET` | `/p/{slug}` | Render a public post page |
| `POST` | `/posts/{id}` | Update a post |
| `DELETE` | `/posts/{id}` | Delete a post |
| `GET` | `/api/tags` | Return tags as JSON |
| `GET` | `/tags/{id}` | Render posts for a tag |
| `POST` | `/tags` | Create a tag |
| `DELETE` | `/tags/{id}` | Delete a tag |

Both `/` and `/api/posts` accept `page`, `size`, `query`, and `sort` parameters. Supported sort values are `newest`, `oldest`, `titleAsc`, and `titleDesc`; page sizes are limited to 50.

## Project structure

```text
controller -> service -> repository -> SQLite
```

- Controllers handle HTTP requests and views.
- Services define transactional business operations.
- Repositories provide persistence through Spring Data JPA.

Licensed under the [Apache License 2.0](LICENSE.md).
