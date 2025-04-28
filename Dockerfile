# Use Eclipse Temurin JDK 21 as the base image for the builder
FROM eclipse-temurin:21-jdk AS builder

# Set working directory for build
WORKDIR /app

# Copying .git directory to support Gradle's gitProperties
#COPY .git .git
COPY src src

# Copy necessary build files
COPY gradlew .
COPY gradle gradle
COPY build.gradle build.gradle
COPY settings.gradle settings.gradle

# Ensure Gradle wrapper has execute permission
RUN chmod +x gradlew

# Pre-download dependencies (this is the caching step)
RUN ./gradlew dependencies

# Build the application with Gradle.
# Disables the Gradle Daemon. As improvement of build performance not required between consecutive build
# across builds.
# disabled the test tasks as part build as build is expected to conect a MySQL DB
RUN ./gradlew build --no-daemon -x test

# Runtime stage using Distroless image
FROM gcr.io/distroless/java21

# Set working directory
WORKDIR /app

# /app/localdata directory to write file in the container
# This is temporary and will be removed
#RUN mkdir /app/localdata &&  \
#    chown -R nonroot:nonroot /app/localdata

# The distroless image already has a nonroot user with UID 65532
USER nonroot

# Copy built application from the builder stage
COPY --from=builder --chown=nonroot:nonroot /app/build/libs/*.jar app.jar
#COPY --from=builder --chown=nonroot:nonroot /app/scripts/ /app/scripts/

# To run the application
#ENTRYPOINT ["/app/scripts/entrypoint.sh"]
ENTRYPOINT ["java", "-jar", "./app.jar"]
