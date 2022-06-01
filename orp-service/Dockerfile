FROM bellsoft/liberica-openjdk-alpine:11.0.5

COPY build/libs/dispatcher.jar /opt/app/dispatcher.jar

WORKDIR /opt/app

CMD java -jar $JVM_OPTS dispatcher.jar