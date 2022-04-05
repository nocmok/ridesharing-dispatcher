package com.nocmok.orp.orp_solver.kafka.orp_input;

import com.nocmok.orp.kafka.orp_input.AssignRequestMessage;
import com.nocmok.orp.kafka.orp_input.ServiceRequestMessage;
import com.nocmok.orp.orp_solver.kafka.orp_input.mapper.AssignRequestMessageMapper;
import com.nocmok.orp.orp_solver.kafka.orp_input.mapper.ServiceRequestMessageMapper;
import com.nocmok.orp.orp_solver.service.dispatching.RequestAssigningService;
import com.nocmok.orp.orp_solver.service.dispatching.ServiceRequestDispatchingService;
import com.nocmok.orp.orp_solver.service.dispatching.ServiceRequestService;
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

    private final ServiceRequestMessageMapper serviceRequestMessageMapper = new ServiceRequestMessageMapper();
    private final AssignRequestMessageMapper assignRequestMessageMapper = new AssignRequestMessageMapper();
    private final ServiceRequestDispatchingService requestProcessingService;
    private final ServiceRequestService serviceRequestService;
    private final RequestAssigningService requestAssigningService;

    @Autowired
    public OrpInputListener(ServiceRequestDispatchingService requestProcessingService,
                            ServiceRequestService serviceRequestService,
                            RequestAssigningService requestAssigningService) {
        this.requestProcessingService = requestProcessingService;
        this.serviceRequestService = serviceRequestService;
        this.requestAssigningService = requestAssigningService;
    }

    @KafkaHandler
    public void receiveServiceRequestMessage(@Payload ServiceRequestMessage message) {
        var serviceRequest = serviceRequestMessageMapper.mapMessageToRequest(message);
        serviceRequestService.insertRequest(serviceRequest);
        requestProcessingService.dispatchServiceRequest(serviceRequest);
    }

    @KafkaHandler
    public void receiveAcceptRequestMessage(@Payload AssignRequestMessage message) {
        requestAssigningService.assignRequest(assignRequestMessageMapper.mapAssignRequestMessageToAssignRequest(message));
    }

    @KafkaHandler(isDefault = true)
    public void fallback(@Payload Object unknownMessage) {
        log.warn("malformed message received\n" + unknownMessage);
    }
}
