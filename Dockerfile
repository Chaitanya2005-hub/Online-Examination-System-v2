# Step 1: Build the application using Maven with Java 21
FROM maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR /app

# Copy Maven configuration and build dependencies
COPY pom.xml .
COPY mvnw .
COPY .mvn .mvn
COPY src ./src

# Build production JAR
RUN ./mvnw clean package -DskipTests

# Step 2: Create lightweight runtime container with Java 21 JRE
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# Copy built JAR from build stage
COPY --from=build /app/target/*.jar app.jar

# Expose standard Spring Boot port
EXPOSE 8080

# Run Spring Boot application
ENTRYPOINT ["java", "-jar", "app.jar"]
