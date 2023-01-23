FROM registry.nextpertise.tools/nextpertise-proxy/library/alpine:latest
COPY ./target/keycloak-sha1.jar keycloak-sha1-salted.jar
