# =========================================
# Etapa 1: build (JDK + Maven)
# =========================================
FROM maven:3.9-eclipse-temurin-17-alpine AS build
WORKDIR /app

# Capa de dependencias: solo cambia si cambia el pom.xml
COPY pom.xml .
RUN mvn -B dependency:go-offline

# Capa de código: cambia en cada modificación del src
COPY src ./src
RUN mvn -B package -DskipTests

# =========================================
# Etapa 2: runtime (solo JRE, sin Maven ni código fuente)
# =========================================
FROM eclipse-temurin:17-jre-alpine AS runtime
WORKDIR /app

# Usuario sin privilegios
RUN addgroup -S spring && adduser -S spring -G spring

COPY --from=build --chown=spring:spring /app/target/*.jar app.jar

USER spring

EXPOSE 8080
ENV JAVA_OPTS="-XX:MaxRAMPercentage=75.0"

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]