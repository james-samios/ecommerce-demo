# Build stage
FROM gradle:8.1.0-jdk17 AS build
WORKDIR /home/gradle
COPY build.gradle.kts settings.gradle.kts ./
COPY gradle ./gradle

# Copy source code and build the project
COPY src ./src
RUN gradle clean build --no-daemon

# Run stage
FROM openjdk:17-jdk-alpine
EXPOSE 8080
COPY --from=build /home/gradle/build/libs/api-1.0.jar /app.jar
ENTRYPOINT ["java","-jar","/app.jar"]