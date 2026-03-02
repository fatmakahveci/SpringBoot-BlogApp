![maven](https://github.com/fatmakahveci/SpringBoot-WebProject/actions/workflows/maven.yml/badge.svg)
![License](https://img.shields.io/badge/License-Apache_2.0-blue.svg)
![Java](https://img.shields.io/badge/java-%2018-brown.svg)
![Spring](https://img.shields.io/badge/Spring%20Boot%20Starter-%202.7.3-green.svg)
![JUnit](https://img.shields.io/badge/JUnit-%204-orange.svg)
![maven](https://img.shields.io/badge/tool-maven-0440af.svg)

![Java](https://img.shields.io/badge/java-%23ED8B00.svg?style=for-the-badge&logo=java&logoColor=white)
![Spring](https://img.shields.io/badge/spring-%236DB33F.svg?style=for-the-badge&logo=spring&logoColor=white)
![Thymeleaf](https://img.shields.io/badge/Thymeleaf-%23005C0F.svg?style=for-the-badge&logo=Thymeleaf&logoColor=white)
![Apache Maven](https://img.shields.io/badge/Apache%20Maven-C71A36?style=for-the-badge&logo=Apache%20Maven&logoColor=white)
![Docker](https://img.shields.io/badge/docker-%230db7ed.svg?style=for-the-badge&logo=docker&logoColor=white)
![GitHub Actions](https://img.shields.io/badge/github%20actions-%232671E5.svg?style=for-the-badge&logo=githubactions&logoColor=white)
![Bootstrap](https://img.shields.io/badge/bootstrap-%23563D7C.svg?style=for-the-badge&logo=bootstrap&logoColor=white)
![Visual Studio Code](https://img.shields.io/badge/Visual%20Studio%20Code-0078d7.svg?style=for-the-badge&logo=visual-studio-code&logoColor=white)
![Markdown](https://img.shields.io/badge/markdown-%23000000.svg?style=for-the-badge&logo=markdown&logoColor=white)

# ✍️ Spring Boot Blog Application

A backend-focused blogging platform exposing RESTful APIs. The goal of this project is to practice layered architecture and build a testable service-oriented backend.

## Demo

![demo.gif](demo.gif)


## Features

- Create, update, delete blog posts
- User management
- RESTful API endpoints
- Layered architecture

## Tech Stack

- Java
- Spring Boot
- Spring Data JPA
- MySQL
- Maven

## Architecture

- Controller → Service → Repository
- The project follows separation of concerns:
- **Controller:** handles HTTP requests
- **Service:** business logic
- **Repository:** database access

## Example Endpoints

- `GET     /posts`
- `POST    /posts`
- `PUT     /posts/{id}`
- `DELETE  /posts/{id}`

## Requirements

- [Java 18+](https://www.oracle.com/java/technologies/javase/jdk18-archive-downloads.html)
- [Maven 3.8.6](https://maven.apache.org/install.html)
- MySQL

## Installation

```bash
git clone https://github.com/fatmakahveci/SpringBoot-WebProject.git
cd SpringBoot-WebProject
mvn clean spring-boot:run
```

- View the index page at `http://localhost:8080/`

## Test

```bash
mvn clean test
```

## Running with Docker

- Pull the image from `docker hub`

```bash
docker pull fatmakhv/springboot:latest
```

- Run the container

```bash
docker run -p 8080:8080 fatmakhv/springboot
```

- View the index page at `http://localhost:8080/`

## What I Learned

- Designing REST APIs
- Structuring a backend project
- Database mapping with JPA
- Error handling
- Building maintainable services

---

Its source code is available with the Apache license and contributions are welcome.
