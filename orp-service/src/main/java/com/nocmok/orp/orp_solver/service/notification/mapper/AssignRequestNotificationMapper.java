package com.nocmok.orp.orp_solver.service.notification.mapper;

import com.nocmok.orp.orp_solver.service.notification.dto.AssignRequestNotification;
import com.nocmok.orp.orp_solver.storage.notification.dto.OrpOutputOutboxRecord;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
public class AssignRequestNotificationMapper {

    /**
     * Возвращает объект без проставления messageId
     */
    public OrpOutputOutboxRecord<AssignRequestNotification> mapAssignRequestNotificationToOrpOutputOutboxRecord(
            AssignRequestNotification assignRequestNotification) {

        return OrpOutputOutboxRecord.<AssignRequestNotification>builder()
                .messageId(null)
                .partitionKey(assignRequestNotification.getVehicleId())
                .payload(assignRequestNotification)
                .createdAt(Instant.now())
                .messageKind(AssignRequestNotification.class.getName())
                .sentAt(null)
                .build();
    }
}
