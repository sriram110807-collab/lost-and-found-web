# ===================================================================
# Dockerfile for Railway Deployment - Lost & Found Web Application
# Multi-stage build with Maven 3.9 & Eclipse Temurin 17 JRE
# ===================================================================

# -------------------------------------------------------------------
# Stage 1: Build the Application
# -------------------------------------------------------------------
FROM maven:3.9.9-eclipse-temurin-17-alpine AS builder

WORKDIR /build

# Cache Maven dependencies by copying pom.xml first
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copy project source code
COPY src ./src

# Build the standalone Spring Boot executable JAR (skip tests during container image build)
RUN mvn clean package -DskipTests -B

# -------------------------------------------------------------------
# Stage 2: Runtime Image
# -------------------------------------------------------------------
FROM eclipse-temurin:17-jre-alpine

WORKDIR /app

# Create upload directory for persistent volume storage
RUN mkdir -p /app/uploads && chmod 777 /app/uploads

# Copy packaged JAR artifact from builder stage
COPY --from=builder /build/target/lost-and-found-web-*.jar app.jar

# Default Environment Variables (Railway automatically overrides PORT and DB vars)
ENV PORT=8080
ENV SPRING_PROFILES_ACTIVE=prod
ENV UPLOAD_DIR=/app/uploads

# Expose default HTTP port
EXPOSE 8080

# Execute Spring Boot application with container-aware memory limits
ENTRYPOINT ["java", "-XX:+UseContainerSupport", "-XX:MaxRAMPercentage=75.0", "-Djava.security.egd=file:/dev/./urandom", "-jar", "app.jar"]
