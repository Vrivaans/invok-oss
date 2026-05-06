FROM eclipse-temurin:21-jre-alpine
RUN mkdir -p /app/data
COPY target/invok-core-*.jar /app/invok-core.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app/invok-core.jar"]
