package com.nocmok.orp.orp_solver.kafka.orp_input;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Component
public class OrpInputListener {

    private static final Logger log = LoggerFactory.getLogger(OrpInputListener.class);
    private MessageDispatcher dispatcher;

    @Autowired
    public OrpInputListener(MessageDispatcher dispatcher) {
        this.dispatcher = dispatcher;
    }

    @KafkaListener(topics = {"orp.input"},
            containerFactory = "orpInputKafkaListenerContainerFactory")
    public void listen(@Header(OrpInputHeaders.REQUEST_TYPE) String requestType, @Payload String payload) {
        try {
            dispatcher.dispatch(requestType, payload);
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }
}
