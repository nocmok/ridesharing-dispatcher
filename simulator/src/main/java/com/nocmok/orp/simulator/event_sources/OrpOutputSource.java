package com.nocmok.orp.simulator.event_sources;

import com.nocmok.orp.kafka.orp_output.AssignRequestNotification;
import com.nocmok.orp.kafka.orp_output.ServiceRequestNotification;
import com.nocmok.orp.simulator.event_bus.EventBus;
import com.nocmok.orp.simulator.event_bus.event.RequestAssignConfirmationEvent;
import com.nocmok.orp.simulator.event_bus.event.ServiceRequestEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@KafkaListener(
        topics = {"orp.output"},
        containerFactory = "orpOutputKafkaListenerContainerFactory"
)
public class OrpOutputSource {

    private EventBus eventBus;

    @Autowired
    public OrpOutputSource(EventBus eventBus) {
        this.eventBus = eventBus;
    }

    @KafkaHandler
    public void listenAssignConfirmationNotification(@Payload AssignRequestNotification assignRequestNotification) {
        log.info("emit " + assignRequestNotification);
        eventBus.emit(RequestAssignConfirmationEvent.builder()
                .sessionId(assignRequestNotification.getSessionId())
                .serviceRequestId(assignRequestNotification.getServiceRequestId())
                .routeScheduled(assignRequestNotification.getRouteScheduled())
                .schedule(assignRequestNotification.getSchedule())
                .build());
    }

    @KafkaHandler
    public void listenServiceRequestNotification(@Payload ServiceRequestNotification serviceRequestNotification) {
        log.info("emit " + serviceRequestNotification);
        eventBus.emit(ServiceRequestEvent.builder()
                .sessionId(serviceRequestNotification.getSessionId())
                .requestId(serviceRequestNotification.getRequestId())
                .reservationId(serviceRequestNotification.getReservationId())
                .build());
    }

    @KafkaHandler(isDefault = true)
    public void processUnknownType(@Payload Object unknown) {
        log.warn("get unknown payload from orp.output topic\n" + unknown);
    }
}
