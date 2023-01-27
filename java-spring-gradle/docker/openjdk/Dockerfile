FROM openjdk:17-alpine as build
WORKDIR /workspace/app

COPY gradle gradle
COPY build.gradle settings.gradle gradlew ./
COPY src src

RUN ./gradlew build -x test


FROM openjdk:17-alpine
WORKDIR /home/application/java
COPY --from=build "/workspace/app/build/libs/*.jar" "./app.jar"
EXPOSE 8080
ENTRYPOINT [ "java", "-jar", "/home/application/java/app.jar"]