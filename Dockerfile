<<<<<<< HEAD
FROM maven:3.9.6-eclipse-temurin-21 AS builder
WORKDIR /app
COPY pom.xml .
RUN mvn dependency:go-offline -B
COPY src ./src
RUN mvn package -DskipTests -B

FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=builder /app/target/*.jar app.jar
EXPOSE 8080
=======
# Etapa 1: compilar
FROM eclipse-temurin:21-jdk-jammy AS build
WORKDIR /app

COPY mvnw pom.xml ./
COPY .mvn .mvn
RUN chmod +x mvnw && ./mvnw dependency:go-offline -q

COPY src ./src
RUN ./mvnw clean package -DskipTests -q

# Etapa 2: ejecutar
FROM eclipse-temurin:21-jre-jammy
WORKDIR /app

COPY --from=build /app/target/*.jar app.jar

EXPOSE 8082
>>>>>>> a28c0a12e67422445e723ae92ff92c31dad62065
ENTRYPOINT ["java", "-jar", "app.jar"]
