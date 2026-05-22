FROM eclipse-temurin:21-jre

COPY main/build/libs/main-1.0-SNAPSHOT.jar /app/ABstract.jar
COPY frontend/dist /app/static

WORKDIR /app

EXPOSE 8080
EXPOSE 7001
EXPOSE 7002

# 运行Spring Boot应用程序
CMD ["java", "-jar", "-Dspring.profiles.active=docker", "/app/ABstract.jar"]