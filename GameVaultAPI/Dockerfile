# =============================================================================
# Stage 1: Build Stage using Eclipse Temurin JDK 25
# =============================================================================
FROM eclipse-temurin:25-jdk AS builder
WORKDIR /app

# Copy Gradle wrapper and configuration files first to leverage Docker layer caching
COPY gradlew .
COPY gradle ./gradle
COPY build.gradle settings.gradle ./

# Ensure Gradle wrapper has execution permissions
RUN chmod +x gradlew

# Download dependencies separately to cache layer
RUN ./gradlew dependencies --no-daemon || true

# Copy application source code
COPY src ./src

# Compile and package Spring Boot executable JAR (skipping tests; CI runs test suite)
RUN ./gradlew bootJar --no-daemon -x test

# =============================================================================
# Stage 2: Minimal & Secure Production Runtime using Eclipse Temurin JRE 25
# =============================================================================
FROM eclipse-temurin:25-jre AS runner
WORKDIR /app

# Install curl for healthcheck verification and remove apt cache
RUN apt-get update && \
    apt-get install -y --no-install-recommends curl && \
    rm -rf /var/lib/apt/lists/*

# Run as a dedicated non-root user for security (defense in depth)
RUN groupadd -r spring && useradd -r -g spring spring
USER spring:spring

# Copy built JAR from the builder stage
COPY --from=builder --chown=spring:spring /app/build/libs/*.jar app.jar

# Expose default Spring Boot application port
EXPOSE 8080

# Configure container healthcheck against the RESTful library endpoint
HEALTHCHECK --interval=30s --timeout=5s --start-period=20s --retries=3 \
  CMD curl -f http://localhost:8080/api/library || exit 1

# Start Spring Boot application
ENTRYPOINT ["java", "-jar", "app.jar"]
