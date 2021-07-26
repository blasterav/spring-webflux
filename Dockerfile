FROM openjdk:8-jdk-alpine
VOLUME /tmp
COPY target/*.jar app.jar
ENTRYPOINT ["sh", "-c", "java -Xms128m -Xmx384m -jar /app.jar"]