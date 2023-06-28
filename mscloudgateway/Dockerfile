FROM maven:3.8.1-openjdk-17-slim as build
WORKDIR /app
COPY . .
RUN mvn clean package -DskipTests



FROM openjdk:17
WORKDIR /app
COPY --from=build ./app/target/*.jar ./app.jar
ARG EUREKA_SERVER=localhost
ARG KEYCLOACK_SERVER=localhost
ARG KEYCLOAK_PORT=8081
ENTRYPOINT java -jar app.jar