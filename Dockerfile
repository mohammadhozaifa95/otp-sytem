# Step 1: Use official Maven image with Java 21 to build the project
FROM maven:3.9.6-eclipse-temurin-21 AS build

# Set working directory inside container
WORKDIR /app

# Copy pom.xml and download dependencies
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copy the entire project source
COPY src ./src

# Build the project
RUN mvn clean package -DskipTests

# Step 2: Use lightweight Java 21 runtime for running the app
FROM eclipse-temurin:21-jdk-jammy

# Set working directory
WORKDIR /app

# Copy the jar from build stage
COPY --from=build /app/target/*.jar app.jar

# Expose default Spring Boot port
EXPOSE 8080

# Command to run the jar
ENTRYPOINT ["java","-jar","app.jar"]
 
