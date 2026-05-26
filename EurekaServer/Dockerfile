FROM eclipse-temurin:26-jdk AS build
WORKDIR /workspace

COPY gradlew gradlew.bat settings.gradle.kts build.gradle.kts ./
COPY gradle ./gradle
COPY src ./src

RUN sed -i 's/\r$//' gradlew && chmod +x gradlew
RUN ./gradlew bootJar --no-daemon

FROM eclipse-temurin:26-jre
WORKDIR /app

COPY --from=build /workspace/build/libs/*.jar app.jar

EXPOSE 8761
ENTRYPOINT ["java", "-jar", "app.jar"]
