FROM maven:3.8.1-openjdk-11

WORKDIR /app

COPY src src
COPY faces-config.NavData .
COPY nb-configuration.xml .
COPY nbactions.xml .
COPY pom.xml .

RUN mvn dependency:go-offline

EXPOSE 8080

CMD ["mvn", "spring-boot:run"]
