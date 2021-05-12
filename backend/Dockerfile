FROM maven:3.8.1-openjdk-11

COPY src /tmp/src
COPY faces-config.NavData /tmp/
COPY nb-configuration.xml /tmp/
COPY nbactions.xml /tmp/
COPY pom.xml /tmp/
COPY Vagrantfile /tmp/
WORKDIR /tmp/
EXPOSE 8080

RUN mvn dependency:go-offline
CMD ["mvn", "spring-boot:run"]
