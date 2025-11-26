# ---- Etapa de build (compila el JAR) ----
FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /app

# Copiamos pom primero para cachear dependencias
COPY pom.xml .
RUN mvn -B -q -DskipTests dependency:go-offline

# Copiamos el código y construimos
COPY src ./src
RUN mvn -B -DskipTests package

# ---- Etapa de runtime (solo JRE) ----
FROM eclipse-temurin:21-jre
WORKDIR /app

# Copiamos el JAR construido (toma el único jar en target)
COPY --from=build /app/target/*.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java","-jar","/app/app.jar"]
