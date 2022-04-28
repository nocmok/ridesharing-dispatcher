package com.nocmok.orp.orp_solver.kafka.orp_input;

import com.nocmok.orp.kafka.orp_input.RequestConfirmationMessage;
import com.nocmok.orp.kafka.orp_input.ServiceRequestMessage;
import com.nocmok.orp.kafka.orp_input.UpdateOrderStatusMessage;
import com.nocmok.orp.orp_solver.kafka.orp_input.mapper.AssignRequestMessageMapper;
import com.nocmok.orp.orp_solver.kafka.orp_input.mapper.ServiceRequestMessageMapper;
import com.nocmok.orp.orp_solver.kafka.orp_input.validator.AssignRequestValidator;
import com.nocmok.orp.orp_solver.kafka.orp_input.validator.ServiceRequestValidator;
import com.nocmok.orp.orp_solver.service.dispatching.RequestAssigningService;
import com.nocmok.orp.orp_solver.service.dispatching.ServiceRequestDispatchingService;
import com.nocmok.orp.orp_solver.service.request_execution.OrderExecutionService;
import com.nocmok.orp.orp_solver.service.request_execution.OrderStatus;
import com.nocmok.orp.orp_solver.service.request_management.ServiceRequestStorageServiceImpl;
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
    private final RequestAssigningService requestAssigningService;
    private final ServiceRequestValidator serviceRequestValidator;
    private final AssignRequestValidator assignRequestValidator;
    private final OrderExecutionService orderExecutionService;

    @Autowired
    public OrpInputListener(ServiceRequestDispatchingService requestProcessingService,
                            RequestAssigningService requestAssigningService,
                            ServiceRequestValidator serviceRequestValidator,
                            AssignRequestValidator assignRequestValidator,
                            OrderExecutionService orderExecutionService) {
        this.requestProcessingService = requestProcessingService;
        this.requestAssigningService = requestAssigningService;
        this.serviceRequestValidator = serviceRequestValidator;
        this.assignRequestValidator = assignRequestValidator;
        this.orderExecutionService = orderExecutionService;
    }

    @KafkaHandler
    public void receiveServiceRequestMessage(@Payload ServiceRequestMessage message) {
        var errors = serviceRequestValidator.validateServiceRequest(message);
        if (!errors.isEmpty()) {
            log.error("invalid service request received: " + message + "\nErrors: " + errors);
            return;
        }
        requestProcessingService.dispatchServiceRequest(serviceRequestMessageMapper.mapMessageToServiceRequestDispatchingServiceDto(message));
    }

    @KafkaHandler
    public void receiveAcceptRequestMessage(@Payload RequestConfirmationMessage message) {
        var errors = assignRequestValidator.validateAssignRequest(message);
        if (!errors.isEmpty()) {
            log.error("invalid assign request received: " + message + "\nErrors: " + errors);
            return;
        }
        requestAssigningService.assignRequest(assignRequestMessageMapper.mapAssignRequestMessageToAssignRequest(message));
    }

    private OrderStatus mapRequestStatusFromMessageToInternalOrderStatus(com.nocmok.orp.kafka.orp_input.OrderStatus orderStatus) {
        switch (orderStatus) {
            case SERVING:
                return OrderStatus.SERVING;
            case SERVED:
                return OrderStatus.SERVED;
            case DENIED:
                return OrderStatus.DENIED;
            case SERVING_DENIED:
                return OrderStatus.SERVING_DENIED;
            default:
                throw new IllegalArgumentException("unknown order status" + orderStatus);
        }
    }

    @KafkaHandler
    public void receiveUpdateOrderStatusMessage(@Payload UpdateOrderStatusMessage message) {
        orderExecutionService.updateOrderStatus(message.getSessionId(), message.getOrderId(),
                mapRequestStatusFromMessageToInternalOrderStatus(message.getUpdatedStatus()));
    }

    @KafkaHandler(isDefault = true)
    public void fallback(@Payload Object unknownMessage) {
        log.warn("malformed message received\n" + unknownMessage);
    }
}
