# --- build stage ---
FROM gradle:8.8.0-jdk21 AS builder
WORKDIR /app
COPY . .
RUN gradle --no-daemon shadowJar

# --- run stage ---
FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=builder /app/build/libs/com.spoonofcode.poa.ktor-poa-all.jar /app/app.jar
ENV APP_PORT=8080
EXPOSE 8080
CMD ["java", "-jar", "/app/app.jar"]