FROM openjdk:17-ea-11-jdk-slim
VOLUME /tmp
COPY build/libs/Gateway-1.0.jar Gateway.jar
ENTRYPOINT ["java", "-jar", "Gateway.jar"]