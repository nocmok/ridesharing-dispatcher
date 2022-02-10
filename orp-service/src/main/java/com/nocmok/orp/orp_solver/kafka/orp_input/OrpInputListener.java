package com.nocmok.orp.orp_solver.kafka.orp_input;

import com.nocmok.orp.orp_solver.kafka.orp_input.dto.MatchVehiclesMessage;
import com.nocmok.orp.orp_solver.kafka.orp_input.mapper.MatchVehiclesMessageMapper;
import com.nocmok.orp.orp_solver.service.RequestProcessingService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Component
@KafkaListener(
        topics = {"orp.input"},
        containerFactory = "orpInputKafkaListenerContainerFactory"
)
@Slf4j
public class OrpInputListener {

    private final MatchVehiclesMessageMapper matchVehiclesMessageMapper = new MatchVehiclesMessageMapper();
    private final RequestProcessingService requestProcessingService;

    @Autowired
    public OrpInputListener(RequestProcessingService requestProcessingService) {
        this.requestProcessingService = requestProcessingService;
    }

    @KafkaHandler
    public void receiveMatchVehiclesMessage(@Payload MatchVehiclesMessage message) {
        requestProcessingService.processRequest(matchVehiclesMessageMapper.mapMessageToRequest(message));
    }

    @KafkaHandler(isDefault = true)
    public void fallback(@Payload Object unknownMessage) {
        log.warn("malformed message received\n" + unknownMessage);
    }
}
