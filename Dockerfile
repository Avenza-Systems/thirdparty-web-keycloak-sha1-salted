# Use Maven with OpenJDK 17 for building
FROM maven:3.8.6-eclipse-temurin-17 AS build
ARG VERSION
WORKDIR /app
COPY . .
RUN mvn clean package

# Use a minimal runtime image
FROM amazoncorretto:17-alpine AS runtime
ARG VERSION
WORKDIR /app
COPY --from=build /app/target/avenza-keycloak-sha1-${VERSION}.jar .

# Make sure the jar is executable
RUN chmod +x /app/avenza-keycloak-sha1-${VERSION}.jar

# Start the Java application
ENTRYPOINT ["java", "-jar", "/app/avenza-keycloak-sha1-${VERSION}.jar"]
