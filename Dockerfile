# Use an official Java runtime as a parent image
FROM openjdk:17-jdk-slim

# Set the working directory inside the container
WORKDIR /app

# Copy the Maven wrapper files
COPY .mvn/ .mvn/
COPY mvnw .
COPY pom.xml .

# Copy the source code
COPY src/ src/

# Build the application
RUN ./mvnw dependency:go-offline
RUN ./mvnw clean install

# Package the application
RUN ./mvnw package

# Expose the port that the application will run on
EXPOSE 8080

# Run the application
ENTRYPOINT ["java", "-jar", "target/conference-scheduler-0.0.1-SNAPSHOT.jar"]
