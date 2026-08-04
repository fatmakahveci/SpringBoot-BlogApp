FROM eclipse-temurin:25-jdk-alpine AS builder

WORKDIR /workspace

COPY .mvn/ .mvn/
COPY mvnw pom.xml ./
RUN ./mvnw -B -DskipTests dependency:go-offline

COPY src/ src/
RUN ./mvnw -B -DskipTests package

FROM eclipse-temurin:25-jre-noble

RUN apt-get update \
    && apt-get upgrade --yes \
    && apt-get install --yes --no-install-recommends ca-certificates wget \
    && rm -rf /var/lib/apt/lists/* \
    && groupadd --gid 10001 spring \
    && useradd --uid 10001 --gid spring --no-create-home --shell /usr/sbin/nologin spring \
    && mkdir -p /applications /data /native \
    && chown -R spring:spring /applications /data /native

WORKDIR /applications

COPY --from=builder --chown=spring:spring /workspace/target/springboot.jar ./springboot.jar

ENV BLOG_DATABASE_PATH=/data/blog.db
ENV SPRING_PROFILES_ACTIVE=prod

VOLUME ["/data"]
EXPOSE 8080

USER 10001:10001

HEALTHCHECK --interval=30s --timeout=5s --start-period=30s --retries=3 \
    CMD wget --quiet --spider http://127.0.0.1:8080/actuator/health/liveness || exit 1

ENTRYPOINT ["java", "-Djava.io.tmpdir=/tmp", "-Dorg.sqlite.tmpdir=/native", "-jar", "springboot.jar"]
