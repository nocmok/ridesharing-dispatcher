FROM bellsoft/liberica-openjdk-alpine:11.0.5

COPY build/libs/api.jar /opt/app/app.jar

WORKDIR /opt/app

CMD java -jar $JVM_OPTS app.jar