# Multi-stage build for Spring Boot Backend
FROM eclipse-temurin:21-jdk-alpine AS builder
WORKDIR /workspace

# Copy Maven wrapper and pom.xml
COPY .mvn/ .mvn/
COPY mvnw pom.xml ./
RUN chmod +x mvnw

# Copy source and package artifact
COPY src/ ./src/
RUN ./mvnw clean package -DskipTests

# Stage 2: Minimal, secure runtime image
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# Create and run as unprivileged user
RUN addgroup -S appgroup && adduser -S appuser -G appgroup
USER appuser

# Copy executable jar from builder stage
COPY --from=builder --chown=appuser:appgroup /workspace/target/*.jar app.jar

ENV SERVER_PORT=8081
EXPOSE 8081

HEALTHCHECK --interval=30s --timeout=5s --start-period=30s --retries=3 \
  CMD wget -qO- http://localhost:8081/api/home || exit 1

ENTRYPOINT ["java", "-Djava.security.egd=file:/dev/./urandom", "-jar", "app.jar"]
