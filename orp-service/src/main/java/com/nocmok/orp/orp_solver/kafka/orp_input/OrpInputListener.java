package com.nocmok.orp.orp_solver.kafka.orp_input;

import com.nocmok.orp.kafka.orp_input.AssignRequestMessage;
import com.nocmok.orp.kafka.orp_input.ServiceRequestMessage;
import com.nocmok.orp.orp_solver.kafka.orp_input.mapper.AssignRequestMessageMapper;
import com.nocmok.orp.orp_solver.kafka.orp_input.mapper.ServiceRequestMessageMapper;
import com.nocmok.orp.orp_solver.kafka.orp_input.validator.AssignRequestValidator;
import com.nocmok.orp.orp_solver.kafka.orp_input.validator.ServiceRequestValidator;
import com.nocmok.orp.orp_solver.service.dispatching.RequestAssigningService;
import com.nocmok.orp.orp_solver.service.dispatching.ServiceRequestDispatchingService;
import com.nocmok.orp.orp_solver.service.request_management.ServiceRequestStorageService;
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
    private final ServiceRequestStorageService serviceRequestService;
    private final RequestAssigningService requestAssigningService;
    private final ServiceRequestValidator serviceRequestValidator;
    private final AssignRequestValidator assignRequestValidator;

    @Autowired
    public OrpInputListener(ServiceRequestDispatchingService requestProcessingService,
                            ServiceRequestStorageService serviceRequestService,
                            RequestAssigningService requestAssigningService,
                            ServiceRequestValidator serviceRequestValidator,
                            AssignRequestValidator assignRequestValidator) {
        this.requestProcessingService = requestProcessingService;
        this.serviceRequestService = serviceRequestService;
        this.requestAssigningService = requestAssigningService;
        this.serviceRequestValidator = serviceRequestValidator;
        this.assignRequestValidator = assignRequestValidator;
    }

    @KafkaHandler
    public void receiveServiceRequestMessage(@Payload ServiceRequestMessage message) {
        var errors = serviceRequestValidator.validateServiceRequest(message);
        if (!errors.isEmpty()) {
            log.error("invalid service request received:" + message + "\nErrors: " + errors);
            return;
        }
        serviceRequestService.storeRequest(serviceRequestMessageMapper.mapMessageToServiceRequestStorageServiceDto(message));
        requestProcessingService.dispatchServiceRequest(serviceRequestMessageMapper.mapMessageToServiceRequestDispatchingServiceDto(message));
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
