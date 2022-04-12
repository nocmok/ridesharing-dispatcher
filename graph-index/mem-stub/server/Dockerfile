FROM bellsoft/liberica-openjdk-alpine:11.0.5

COPY build/libs/server-0.0.1.jar /opt/graph_index/server-0.0.1.jar

WORKDIR /opt/graph_index

CMD java -jar $JVM_OPTS server-0.0.1.jar