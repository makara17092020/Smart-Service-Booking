# Stage 1: Build the app with Maven on Java 21
FROM maven:3.9-amazoncorretto-21 AS build
WORKDIR /app

# Copy project files
COPY pom.xml .
COPY src ./src

# Build the application
RUN mvn clean package -DskipTests

# Stage 2: Run the app on Java 21 (Alpine for smaller image size)
FROM amazoncorretto:21-alpine
WORKDIR /app

# Copy the built JAR from the build stage
COPY --from=build /app/target/*.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]