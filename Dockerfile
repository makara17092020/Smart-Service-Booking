# Multi-stage Dockerfile: build inside a Maven+Temurin image, then copy the
# resulting JAR into a lightweight runtime image. This works on Render and
# other container builders because the image itself performs the Maven build.

# Stage 1: build with Maven using Temurin 21 (compatible with Lombok/annotation processors)
FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /app

# Copy project files; copying pom.xml first helps leverage Docker cache for dependencies
COPY pom.xml ./
COPY src ./src

# Tune Maven JVM for constrained builder environments (Render) and limit parallelism
# to reduce memory pressure. Adjust values if your builder allows more RAM.
ENV MAVEN_OPTS="-Xmx1024m -XX:MaxMetaspaceSize=256m"

# Build the application (skip tests for speed in CI/redeploys). Use -T1C to avoid
# aggressive parallel builds which can increase memory usage.
RUN mvn -B -DskipTests -T 1C clean package

# Stage 2: runtime image
FROM amazoncorretto:21-alpine
WORKDIR /app

# Copy the built JAR from the build stage
COPY --from=build /app/target/*.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
