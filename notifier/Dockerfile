FROM bellsoft/liberica-openjdk-alpine:11.0.5

COPY build/libs/notifier-0.0.1.jar /opt/orp-notifier/service.jar

WORKDIR /opt/orp-notifier

CMD java -jar $JVM_OPTS service.jar