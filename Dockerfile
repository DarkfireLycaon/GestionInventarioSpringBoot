# Paso 1: Construcción con Java 21
FROM maven:3.9.6-eclipse-temurin-21 AS build
COPY . .
RUN mvn clean package -DskipTests

# Paso 2: Ejecución con Java 21 (ligero)
FROM eclipse-temurin:21-jre-alpine
COPY --from=build /target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","app.jar"]