FROM eclipse-temurin:17-jdk-alpine

ARG JAR_FILE=dist/*.jar
COPY ./dist/Rentals.jar app.jar

ENTRYPOINT ["java", "-jar", "/app.jar"]
