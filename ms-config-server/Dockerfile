FROM eclipse-temurin:25-jdk-alpine AS builder

WORKDIR /app

COPY .mvn .mvn
COPY mvnw .
COPY pom.xml .

RUN chmod +x mvnw
RUN ./mvnw dependency:go-offline -B

COPY src src

RUN ./mvnw clean package -DskipTests


FROM eclipse-temurin:25-jre-alpine

WORKDIR /app

COPY --from=builder /app/target/*.jar ms-config-server-0.0.1-SNAPSHOT.jar

EXPOSE 7777

ENTRYPOINT ["java", "-jar", "ms-config-server-0.0.1-SNAPSHOT.jar"]