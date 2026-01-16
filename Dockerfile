## Runtime-only Dockerfile
## NOTE: This Dockerfile expects the application JAR to be built locally (target/*.jar).
## Build the project locally first: ./mvnw -DskipTests package

FROM amazoncorretto:21-alpine
WORKDIR /app

# Copy the locally-built JAR into the image
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} app.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]