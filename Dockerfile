# Stage 1: Build the application
FROM maven:3.8.7-eclipse-temurin-8 AS build
WORKDIR /app
COPY pom.xml .
# Download dependencies first for caching
RUN mvn dependency:go-offline
COPY src ./src
RUN mvn clean package -DskipTests

# Stage 2: Run the application
FROM eclipse-temurin:8-jre
WORKDIR /app
COPY --from=build /app/target/airline-reservation-system-1.0-SNAPSHOT.jar app.jar
EXPOSE 9090
ENTRYPOINT ["java", "-jar", "app.jar"]
