FROM openjdk:21

WORKDIR app

COPY pom.xml .
COPY mvnw .
COPY .mvn .mvn
COPY src src

RUN ./mvnw clean package -DskipTests

ENTRYPOINT ["java", "-jar", "/app/target/PddExamBotApi-0.0.1-SNAPSHOT.jar"]
