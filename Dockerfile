FROM openjdk:17-jdk-slim
WORKDIR /app
EXPOSE 8084
COPY target/notification-service-*.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]
