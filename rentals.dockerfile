FROM eclipse-temurin:17-jdk-alpine AS build

WORKDIR /Rental_Management_System

COPY gradlew settings.gradle build.gradle ./
COPY gradle gradle/

COPY src src/

RUN chmod +x gradlew && ./gradlew bootJar -x test


FROM eclipse-temurin:17-jre-alpine

COPY --from=build /build/libs/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java","-jar","/app.jar"]