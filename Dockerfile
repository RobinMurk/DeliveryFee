FROM openjdk:21-jdk-slim

WORKDIR /app

COPY build/libs/app.jar /app/app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-Dserver.address=0.0.0.0", "-jar", "app.jar"]