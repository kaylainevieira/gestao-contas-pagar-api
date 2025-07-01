FROM maven:3.9-amazoncorretto-21-alpine AS builder

WORKDIR /app

COPY pom.xml .

COPY src ./src

RUN mvn clean install -DskipTests -Dmaven.compiler.source=21 -Dmaven.compiler.target=21

FROM alpine/java:21-jre

WORKDIR /app

COPY --from=builder /app/target/gestao-contas-pagar-api-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar", "--spring.profiles.active=docker"]
