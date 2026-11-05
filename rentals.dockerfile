FROM openjdk:17-jdk-slim

WORKDIR /Rental_Management_System

ARG JAR_FILE=build/libs/app-0.0.1-SNAPSHOT-plain.jar
COPY ${JAR_FILE} app-0.0.1-SNAPSHOT-plain.jar

EXPOSE 8080

ENTRYPOINT ["java","-jar","/app-0.0.1-SNAPSHOT-plain.jar"]
