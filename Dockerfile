# --- Build stage ---
FROM gradle:8.8-jdk17 AS build
WORKDIR /src
COPY . .
RUN gradle --no-daemon clean shadowJar

# --- Runtime stage (distroless, nonroot) ---
FROM gcr.io/distroless/java17-debian12:nonroot
WORKDIR /app
# kopiujemy fat-jar z Shadow plugin, np. *-all.jar
COPY --from=build /src/build/libs/*-all.jar /app/app.jar
ENV PORT=8080
# bezpieczniejsze limity pamięci wewnątrz kontenera
ENV JAVA_TOOL_OPTIONS="-XX:+ExitOnOutOfMemoryError -XX:MaxRAMPercentage=75"
EXPOSE 8080
USER nonroot:nonroot
ENTRYPOINT ["java","-jar","/app/app.jar"]