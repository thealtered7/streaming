# Build stage
FROM gradle:8.14.3-jdk17 AS build
WORKDIR /app
COPY . .
RUN gradle build --no-daemon

# Runtime stage
FROM eclipse-temurin:17-jre
WORKDIR /app

# Create a non-root user
RUN addgroup --system --gid 1001 appuser && \
    adduser --system --uid 1001 --ingroup appuser appuser

# Copy the built JAR from build stage (Spring Boot executable JAR)
COPY --from=build /app/build/libs/streaming-*.jar /tmp/
RUN find /tmp -name "streaming-*.jar" -not -name "*-plain.jar" -exec cp {} /app/app.jar \;

# Change ownership to non-root user
RUN chown -R appuser:appuser /app
USER appuser

# Expose the application port
EXPOSE 8080

# Set JVM options for containerized environment
ENV JAVA_OPTS="-Xmx512m -Xms256m"

# Run the application
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"] 