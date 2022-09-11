#
# Build stage
#
FROM maven:3.8-openjdk-8 AS build
COPY src /home/app/src
COPY pom.xml /home/app
RUN mvn -f /home/app/pom.xml clean package compile assembly:single

#
# Package stage
#
FROM adoptopenjdk/openjdk8
COPY --from=build /home/app/src/main/resources/test.properties /usr/local/test.properties
COPY --from=build /home/app/src/main/resources/prod.properties /usr/local/prod.properties
COPY --from=build /home/app/target/paypalEventsHandler-1.0-SNAPSHOT-jar-with-dependencies.jar /usr/local/lib/demo.jar
EXPOSE 8081
ENTRYPOINT exec java $JAVA_OPTS  -jar /usr/local/lib/demo.jar