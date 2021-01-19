FROM maven:3.6.3-jdk-13 AS build
COPY src /usr/src/app/src
COPY pom.xml /usr/src/app
RUN mvn -f /usr/src/app/pom.xml clean package

FROM openjdk:13-jdk
COPY --from=build /usr/src/app/target/APIGateway-1.0-SNAPSHOT.jar /usr/app/APIGateway-1.0-SNAPSHOT.jar
ENV NEO4J_URL=bolt://localhost:7687
EXPOSE 8953
ENTRYPOINT ["java","-jar","/usr/app/APIGateway-1.0-SNAPSHOT.jar"]