# Stage 1: Cache dependencies
FROM maven:3.9-eclipse-temurin-17 AS deps
WORKDIR /app
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Stage 2: Run unit tests (used by CI Test stage)
FROM deps AS test
COPY src ./src
RUN mvn test -B -o

# Stage 3: Build uber-jar (reuses the already-tested layer)
FROM test AS build
RUN mvn package -Dquarkus.package.type=uber-jar -DskipTests -o

# Stage 4: Lightweight runtime image
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
COPY --from=build /app/target/*-runner.jar /app/app.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app/app.jar"]