FROM maven:3.9.4-eclipse-temurin-21 AS build
WORKDIR /workspace

# copy sources and build
COPY . .

RUN mvn -B -DskipTests package

FROM eclipse-temurin:21-jre
WORKDIR /app

# Copy fat jar from build stage (assumes Spring Boot builds an executable jar into target/)
COPY --from=build /workspace/target/*.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
