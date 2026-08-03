FROM eclipse-temurin:21-jdk-alpine AS builder

WORKDIR /workspace

COPY .mvn/ .mvn/
COPY mvnw pom.xml ./
RUN ./mvnw -B -DskipTests dependency:go-offline

COPY src/ src/
RUN ./mvnw -B -DskipTests package

FROM eclipse-temurin:21-jre-alpine

RUN apk upgrade --no-cache \
    && apk add --no-cache curl \
    && addgroup -S spring \
    && adduser -S spring -G spring \
    && mkdir -p /applications /data \
    && chown -R spring:spring /applications /data

WORKDIR /applications

COPY --from=builder --chown=spring:spring /workspace/target/springboot.jar ./springboot.jar

ENV BLOG_DATABASE_PATH=/data/blog.db
ENV SPRING_PROFILES_ACTIVE=prod

VOLUME ["/data"]
EXPOSE 8080

USER spring:spring

HEALTHCHECK --interval=30s --timeout=5s --start-period=30s --retries=3 \
    CMD curl --fail --silent --show-error http://127.0.0.1:8080/actuator/health > /dev/null || exit 1

ENTRYPOINT ["java", "-Djava.io.tmpdir=/tmp", "-jar", "springboot.jar"]
