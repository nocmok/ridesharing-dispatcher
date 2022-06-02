package com.nocmok.orp.orp_solver.service.notification.mapper;

import com.nocmok.orp.orp_solver.service.notification.dto.ServiceRequestNotification;
import com.nocmok.orp.postgres.storage.dto.OrpOutputOutboxRecord;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
public class ServiceRequestNotificationMapper {

    public OrpOutputOutboxRecord<ServiceRequestNotification> mapServiceRequestNotificationToOrpOutboxRecord(
            ServiceRequestNotification serviceRequestNotification) {
        return OrpOutputOutboxRecord.<ServiceRequestNotification>builder()
                .messageId(null)
                .partitionKey(serviceRequestNotification.getSessionId())
                .messageKind(com.nocmok.orp.kafka.orp_output.ServiceRequestNotification.class.getName())
                .createdAt(Instant.now())
                .sentAt(null)
                .payload(serviceRequestNotification)
                .build();
    }
}
