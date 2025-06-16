# Dockerfile

# Base image có JDK 21
FROM eclipse-temurin:21-jdk AS build

WORKDIR /app

# Copy source code và cấp quyền cho mvnw
COPY . .
RUN chmod +x mvnw

# Build dự án
RUN ./mvnw clean package -DskipTests

# Image chạy app
FROM eclipse-temurin:21-jdk

WORKDIR /app

COPY --from=build /app/target/*.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]