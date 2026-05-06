FROM eclipse-temurin:21-jdk-alpine AS builder
WORKDIR /workspace
COPY mvnw pom.xml ./
COPY .mvn .mvn
RUN ./mvnw dependency:go-offline -B
COPY src src
RUN ./mvnw package -DskipTests -B

FROM eclipse-temurin:21-jre-alpine
RUN addgroup -S invok && adduser -S invok -G invok
RUN mkdir -p /app/data && chown -R invok:invok /app
WORKDIR /app
COPY --from=builder /workspace/target/invok-core-*.jar invok-core.jar
EXPOSE 8080
USER invok
ENTRYPOINT ["java", "-jar", "/app/invok-core.jar"]
