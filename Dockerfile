# --- build stage ---
FROM gradle:8.8.0-jdk21 AS builder
WORKDIR /app
COPY . .
RUN gradle --no-daemon shadowJar

# --- run stage ---
FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=builder /app/build/libs/com.spoonofcode.poa.ktor-poa-all.jar /app/app.jar
COPY src/main/resources/application-docker.conf /app/application-docker.conf
CMD ["java","-jar","/app/app.jar","-config=/app/application-docker.conf"]