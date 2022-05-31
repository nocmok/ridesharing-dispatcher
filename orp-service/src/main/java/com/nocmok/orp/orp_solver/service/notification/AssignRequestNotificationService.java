package com.nocmok.orp.orp_solver.service.notification;

import com.nocmok.orp.kafka.orp_output.RequestAssignmentFailedNotification;
import com.nocmok.orp.orp_solver.service.notification.dto.AssignRequestNotification;
import com.nocmok.orp.orp_solver.service.notification.mapper.AssignRequestNotificationMapper;
import com.nocmok.orp.postgres.storage.OrpOutputOutboxStorage;
import com.nocmok.orp.postgres.storage.dto.OrpOutputOutboxRecord;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Slf4j
@Service
public class AssignRequestNotificationService {

    private AssignRequestNotificationMapper assignRequestNotificationMapper;
    private OrpOutputOutboxStorage orpOutputOutboxStorage;

    @Autowired
    public AssignRequestNotificationService(AssignRequestNotificationMapper assignRequestNotificationMapper,
                                            OrpOutputOutboxStorage orpOutputOutboxStorage) {
        this.assignRequestNotificationMapper = assignRequestNotificationMapper;
        this.orpOutputOutboxStorage = orpOutputOutboxStorage;
    }

    /**
     * Кладет сообщение в outbox таблицу для orp.output топика
     */
    public void sendNotification(AssignRequestNotification assignRequestNotification) {
        var record = assignRequestNotificationMapper.mapAssignRequestNotificationToOrpOutputOutboxRecord(assignRequestNotification);
        log.info("notification scheduled " + record.getPayload());
        orpOutputOutboxStorage.insertOneRecord(record);
    }

    public void sendNotification(RequestAssignmentFailedNotification requestAssignmentFailedNotification) {
        var record = OrpOutputOutboxRecord.builder()
                .messageId(null)
                .partitionKey(requestAssignmentFailedNotification.getSessionId())
                .payload(requestAssignmentFailedNotification)
                .createdAt(Instant.now())
                .messageKind(com.nocmok.orp.kafka.orp_output.RequestAssignmentFailedNotification.class.getName())
                .sentAt(null)
                .build();
        log.info("notification scheduled " + record.getPayload());
        orpOutputOutboxStorage.insertOneRecord(record);
    }
}
