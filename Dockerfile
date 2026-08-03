FROM eclipse-temurin:21-jdk-alpine AS builder

WORKDIR /workspace

COPY .mvn/ .mvn/
COPY mvnw pom.xml ./
RUN ./mvnw -B -DskipTests dependency:go-offline

COPY src/ src/
RUN ./mvnw -B -DskipTests package

FROM eclipse-temurin:21-jre-alpine

RUN addgroup -S -g 10001 spring \
    && adduser -S -D -H -u 10001 -G spring spring \
    && mkdir -p /applications /data \
    && chown -R spring:spring /applications /data

WORKDIR /applications

COPY --from=builder --chown=spring:spring /workspace/target/springboot.jar ./springboot.jar

ENV BLOG_DATABASE_PATH=/data/blog.db
ENV SPRING_PROFILES_ACTIVE=prod

VOLUME ["/data"]
EXPOSE 8080

USER 10001:10001

HEALTHCHECK --interval=30s --timeout=5s --start-period=30s --retries=3 \
    CMD wget --quiet --spider http://127.0.0.1:8080/actuator/health || exit 1

ENTRYPOINT ["java", "-Djava.io.tmpdir=/tmp", "-jar", "springboot.jar"]
