# Multi-stage Build
FROM gradle:8.5-jdk21 AS builder
WORKDIR /app
COPY build.gradle settings.gradle ./
COPY src ./src
RUN gradle build -x test --no-daemon

FROM eclipse-temurin:21-jdk
WORKDIR /app

# Persistence for file uploads
VOLUME /app/uploads

# Expose default port (can be overridden)
EXPOSE 8080

COPY --from=builder /app/build/libs/*.jar app.jar

# Run with profiles passed via environment or args
# Usage: docker run -e SPRING_PROFILES_ACTIVE=prod ...
ENTRYPOINT ["java", "-jar", "app.jar"]

