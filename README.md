![maven](https://github.com/fatmakahveci/SpringBoot-WebProject/actions/workflows/maven.yml/badge.svg)
![License](https://img.shields.io/badge/License-Apache_2.0-blue.svg)
![Java](https://img.shields.io/badge/java-%2018-brown.svg)
![Spring](https://img.shields.io/badge/Spring%20Boot%20Starter-%202.7.3-green.svg)
![JUnit](https://img.shields.io/badge/JUnit-%204-orange.svg)
![maven](https://img.shields.io/badge/tool-maven-0440af.svg)

![Java](https://img.shields.io/badge/java-%23ED8B00.svg?style=for-the-badge&logo=java&logoColor=white)
![Spring](https://img.shields.io/badge/spring-%236DB33F.svg?style=for-the-badge&logo=spring&logoColor=white)
![Thymeleaf](https://img.shields.io/badge/Thymeleaf-%23005C0F.svg?style=for-the-badge&logo=Thymeleaf&logoColor=white)
![MySQL](https://img.shields.io/badge/mysql-%2300f.svg?style=for-the-badge&logo=mysql&logoColor=white)
![Apache Maven](https://img.shields.io/badge/Apache%20Maven-C71A36?style=for-the-badge&logo=Apache%20Maven&logoColor=white)
![Docker](https://img.shields.io/badge/docker-%230db7ed.svg?style=for-the-badge&logo=docker&logoColor=white)
![GitHub Actions](https://img.shields.io/badge/github%20actions-%232671E5.svg?style=for-the-badge&logo=githubactions&logoColor=white)
![Bootstrap](https://img.shields.io/badge/bootstrap-%23563D7C.svg?style=for-the-badge&logo=bootstrap&logoColor=white)
![Visual Studio Code](https://img.shields.io/badge/Visual%20Studio%20Code-0078d7.svg?style=for-the-badge&logo=visual-studio-code&logoColor=white)
![Markdown](https://img.shields.io/badge/markdown-%23000000.svg?style=for-the-badge&logo=markdown&logoColor=white)

# A Simple Blog (Spring Boot)

A simple blog to practice Spring Boot, ThymeLeaf, Java, MySQL, Maven, and JUnit.

## Demo

![demo.gif](demo.gif)

## Requirements

- [Java 18+](https://www.oracle.com/java/technologies/javase/jdk18-archive-downloads.html)
- [Maven 3.8.6](https://maven.apache.org/install.html)

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

---

Its source code is available with the Apache license and contributions are welcome.
