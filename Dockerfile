# Use the official OpenJDK 21 base image
FROM amazoncorretto:21

COPY target/authservice-1.0.0.jar authservice-1.0.0.jar

ENTRYPOINT ["java", "-jar", "/authservice-1.0.0.jar"]
