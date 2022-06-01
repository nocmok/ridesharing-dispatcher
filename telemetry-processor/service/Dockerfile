FROM bellsoft/liberica-openjdk-alpine:11.0.5

COPY build/libs/telemetry.jar /opt/app/telemetry.jar

WORKDIR /opt/app

CMD java -jar $JVM_OPTS telemetry.jar