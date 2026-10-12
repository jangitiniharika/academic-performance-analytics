# syntax=docker/dockerfile:1
# ==================================================================
# Academic Performance Analytics & Prediction Platform - backend
# Multi-stage Docker build for the Spring Boot REST API.
#
#   Stage 1 (build)   : Maven + JDK 17 compiles the project and
#                       produces the executable Spring Boot jar.
#   Stage 2 (runtime) : a slim JRE 17 image runs only the jar.
#
# Tests are skipped during the image build (-DskipTests) because the
# integration tests need a live MySQL that is not available inside the
# Docker build context; run them separately with a database (see README).
# ==================================================================

# ---------- Stage 1: build the executable jar ----------
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /workspace

# Cache dependencies first so rebuilds are fast when only sources change.
COPY pom.xml ./
RUN mvn -B -q -DskipTests dependency:go-offline

# Compile and package the Spring Boot jar.
COPY src ./src
RUN mvn -B -DskipTests clean package

# ---------- Stage 2: minimal runtime ----------
FROM eclipse-temurin:17-jre
WORKDIR /app

# Run as a non-root user.
RUN groupadd --system spring && useradd --system --gid spring spring

# Copy the executable jar (the Spring Boot repackaged artifact).
COPY --from=build /workspace/target/academic-performance-platform-0.0.1-SNAPSHOT.jar app.jar

USER spring

# The app listens on 8080 by default. Hosting platforms (Render, Railway,
# Heroku, Fly, ...) inject their own port via the PORT environment variable,
# which we map onto Spring Boot's --server.port.
EXPOSE 8080
ENTRYPOINT ["sh", "-c", "exec java -jar /app/app.jar --server.port=${PORT:-8080}"]
