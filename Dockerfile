## Runtime-only Dockerfile
## NOTE: This Dockerfile expects the application JAR to be built locally (target/*.jar).
## Build the project locally first: ./mvnw -DskipTests package

# Runtime-only image: copy a locally-built JAR produced by running
# "./mvnw -DskipTests clean package" (or via a containerized Maven build).
FROM amazoncorretto:21-alpine
WORKDIR /app

# Copy pre-built application JAR into the image. Make sure you run
# "./mvnw -DskipTests clean package" (or the containerized build) before
# running docker build/compose so target/*.jar exists.
COPY target/*.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]