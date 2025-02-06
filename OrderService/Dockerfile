FROM openjdk:17-ea-11-jdk-slim
VOLUME /tmp
COPY build/libs/OrderService-1.0.jar OrderService.jar
ENTRYPOINT ["java", "-jar", "OrderService.jar"]