FROM eclipse-temurin:21-jre-alpine

EXPOSE 8080

WORKDIR /applications

# Run the application without root privileges if the container is compromised.
RUN addgroup -S spring && adduser -S spring -G spring

COPY target/springboot.jar /applications/springboot.jar

RUN chown -R spring:spring /applications

USER spring:spring

ENTRYPOINT ["java", "-jar", "springboot.jar"]
