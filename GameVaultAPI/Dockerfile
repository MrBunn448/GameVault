# Java 25 JDK image as the build stage.
# The stage is named "builder" to clarify.
FROM eclipse-temurin:25-jdk AS builder
WORKDIR /app

# Copy the entire project.
COPY . .

# Make the Gradle wrapper executable and build the application JAR.
# "bootJar" creates the runnable Spring Boot JAR file.
# "--no-daemon" disables the Gradle daemon to avoid background processes.
# "-x test" skips running tests during the image build.
RUN chmod +x gradlew && ./gradlew bootJar --no-daemon -x test

# Start a second build stage using the smaller Java 25 JRE image.
# This runtime image is used to run the app, not to compile it.
FROM eclipse-temurin:25-jre AS runner

# Set the working directory inside the runtime container to /app.
WORKDIR /app

# Copy the built .jar file from the builder stage into this stage.
# The copied file is renamed to "app.jar" for easier execution.
COPY --from=builder /app/build/libs/*.jar app.jar

EXPOSE 8080
# run the java executable.
CMD ["java", "-jar", "app.jar"]