package com.nocmok.orp.orp_solver;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class Echoer {

    private static Logger log = LoggerFactory.getLogger(Echoer.class);

    @Autowired
    private KafkaTemplate<String, String> producer;

    @KafkaListener(topics = "orp.input", groupId = "orp_solver")
    public void echo(String message) {
        log.info("received:  " + message);
        producer.send("orp.output", message);
        log.info("sent: " + message);
    }


}
