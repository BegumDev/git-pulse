# 1. Build the application package using Maven
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app

# Cache dependencies in a separate layer first
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copy source code and package (offline mode uses the cached dependencies)
COPY src ./src
RUN mvn package -Dquarkus.package.type=uber-jar -DskipTests -o

# 2. Run the application using a lightweight Alpine Java runtime
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
COPY --from=build /app/target/*-runner.jar /app/app.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app/app.jar"]