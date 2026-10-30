# 1. Build the application package using Maven
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -Dquarkus.package.type=uber-jar -DskipTests

# 2: Run the application using a lightweight Java runtime
FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=build /app/target/*-runner.jar /app/app.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
