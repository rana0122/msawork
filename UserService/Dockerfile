FROM openjdk:17-ea-11-jdk-slim
VOLUME /tmp
COPY build/libs/UserService-1.0.jar UserService.jar
ENTRYPOINT ["java", "-jar", "UserService.jar"]