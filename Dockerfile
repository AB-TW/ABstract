FROM openjdk:17.0.1-jdk-slim

COPY main/build/libs/main-1.0-SNAPSHOT.jar /app/ABstract.jar

WORKDIR /app

EXPOSE 8080

# 运行Spring Boot应用程序
CMD ["java", "-jar", "-Dspring.profiles.active=local", "/app/ABstract.jar"]