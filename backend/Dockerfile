FROM openjdk:17-jdk-slim

WORKDIR /app

# Copy the Maven wrapper and pom.xml
COPY mvnw .
COPY mvnw.cmd .
COPY .mvn .mvn
COPY pom.xml .

# Make Maven wrapper executable and fix potential line ending issues
RUN sed -i 's/\r$//' mvnw && chmod +x mvnw

# Download dependencies (this will be cached if pom.xml doesn't change)
RUN ./mvnw dependency:go-offline -B

# Copy source code
COPY src src

# Copy start script before building
COPY start.sh start.sh

# Build the application
RUN ./mvnw clean package -DskipTests

# Move the built JAR to a predictable location and make start script executable
RUN mv target/portal-0.0.1-SNAPSHOT.jar app.jar && chmod +x start.sh

# Expose port
EXPOSE 8080

# Use start script to handle DATABASE_URL conversion
CMD ["./start.sh"]