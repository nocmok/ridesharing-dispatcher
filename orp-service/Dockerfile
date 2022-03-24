FROM bellsoft/liberica-openjdk-alpine:11.0.5

COPY build/libs/orp-service-0.0.1-SNAPSHOT.jar /opt/orp-dispatcher/service.jar

WORKDIR /opt/orp-dispatcher

CMD java -jar $JVM_OPTS service.jar