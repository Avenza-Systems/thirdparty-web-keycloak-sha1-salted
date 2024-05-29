FROM maven:3.6.3-openjdk-11 as maven

WORKDIR /app

COPY . .

RUN mvn package

FROM scratch AS export-stage
COPY --from=maven /app/target/keycloak-sha1.jar .
