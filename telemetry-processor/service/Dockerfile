FROM bellsoft/liberica-openjdk-alpine:11.0.5

COPY build/libs/service-0.0.1-SNAPSHOT.jar /opt/telemetry-processor/service.jar

WORKDIR /opt/telemetry-processor

CMD java -jar $JVM_OPTS service.jar