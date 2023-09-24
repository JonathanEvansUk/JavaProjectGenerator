# Use a lightweight, Alpine-based image of the latest OpenJDK 17
#FROM --platform=linux/amd64 eclipse-temurin:17-jre-alpine
FROM arm64v8/eclipse-temurin:17-jre

# Set the working directory to /app
WORKDIR /app

# Copy the Spring Boot app JAR file to the container
COPY target/testproject-1.0-SNAPSHOT.jar testproject.jar

# Run the Spring Boot app when the container starts
CMD ["java", "-jar", "testproject.jar"]
