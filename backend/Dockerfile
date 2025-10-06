FROM openjdk:17-jdk-slim

WORKDIR /app

# Copy the Maven wrapper and pom.xml
COPY mvnw .
COPY mvnw.cmd .
COPY .mvn .mvn
COPY pom.xml .

# Download dependencies (this will be cached if pom.xml doesn't change)
RUN ./mvnw dependency:go-offline -B

# Copy source code
COPY src src

# Build the application
RUN ./mvnw clean package -DskipTests

# Copy the built JAR file and start script
COPY target/portal-0.0.1-SNAPSHOT.jar app.jar
COPY start.sh start.sh

# Make start script executable
RUN chmod +x start.sh

# Expose port
EXPOSE 8080

# Use start script to handle DATABASE_URL conversion
CMD ["./start.sh"]