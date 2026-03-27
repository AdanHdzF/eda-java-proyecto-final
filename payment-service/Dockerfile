# ── Build stage ──
FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /build

# Install shared-kernel first
COPY shared-kernel/pom.xml shared-kernel/pom.xml
COPY shared-kernel/src shared-kernel/src
RUN mvn -f shared-kernel/pom.xml install -q -DskipTests

# Build this service
COPY payment-service/pom.xml pom.xml
COPY payment-service/src src
RUN mvn package -q -DskipTests \
 && rm -f target/original-*.jar

# ── Run stage ──
FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=build /build/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
