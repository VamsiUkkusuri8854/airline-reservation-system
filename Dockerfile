# ==========================================================
# STAGE 1: Compilation and packaging using Maven
# ==========================================================
FROM maven:3.8.1-openjdk-8 AS build
WORKDIR /app

# Pre-fetch dependencies to leverage Docker cache layers
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copy source code and build the final executable JAR file
COPY src ./src
RUN mvn clean package -DskipTests -B

# ==========================================================
# STAGE 2: Lightweight runtime image
# ==========================================================
FROM openjdk:8-jre-alpine
WORKDIR /app

# Copy packaged jar from build container stage
COPY --from=build /app/target/airline-reservation-system-1.0-SNAPSHOT.jar app.jar

# Define system parameters and application port
ENV PORT=8080
EXPOSE 8080

# Run the containerized executable
ENTRYPOINT ["java", "-Djava.security.egd=file:/dev/./urandom", "-jar", "app.jar"]
