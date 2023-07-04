FROM maven:3.8.1-openjdk-17-slim as build
WORKDIR /app
COPY . .
RUN mvn clean package -DskipTests



FROM openjdk:17
WORKDIR /app
COPY --from=build ./app/target/*.jar ./app.jar
ARG EUREKA_SERVER
ENTRYPOINT java -jar -Dspring.profiles.active=prod app.jar