FROM openjdk:18

EXPOSE 8080

WORKDIR /applications

COPY target/springboot.jar /applications/springboot.jar

ENTRYPOINT ["java","-jar", "sample-application.jar"]
