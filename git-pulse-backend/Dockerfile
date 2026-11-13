# Stage 1: Cache dependencies
FROM maven:3.9-eclipse-temurin-17 AS deps
WORKDIR /app
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Stage 2: Run unit tests (downloads any remaining test-scoped plugins/JARs)
FROM deps AS test
COPY src ./src
RUN mvn test -B

# Stage 3: Build uber-jar (all compile + test deps are now cached, so -o works cleanly)
FROM test AS build
RUN mvn package -Dquarkus.package.type=uber-jar -DskipTests -o

# Stage 4: Lightweight runtime image
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
COPY --from=build /app/target/*-runner.jar /app/app.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app/app.jar"]