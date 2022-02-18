package com.nocmok.orp.orp_solver.service.notification;

import com.nocmok.orp.orp_solver.service.notification.dto.AssignRequestNotification;
import com.nocmok.orp.orp_solver.service.notification.mapper.AssignRequestNotificationMapper;
import com.nocmok.orp.orp_solver.storage.notification.OrpOutputOutboxStorage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
        orpOutputOutboxStorage.insertOneRecord(record);
    }
}
