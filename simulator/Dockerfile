FROM bellsoft/liberica-openjdk-alpine:11.0.5

COPY build/libs/simulator.jar /opt/app/simulator.jar

WORKDIR /opt/app

CMD java -jar $JVM_OPTS simulator.jar