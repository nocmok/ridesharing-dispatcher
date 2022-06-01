FROM bellsoft/liberica-openjdk-alpine:11.0.5

COPY build/libs/notifier.jar /opt/app/notifier.jar

WORKDIR /opt/app

CMD java -jar $JVM_OPTS notifier.jar