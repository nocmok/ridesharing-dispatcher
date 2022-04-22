package com.nocmok.orp.orp_solver.service.notification;

import com.nocmok.orp.orp_solver.service.notification.dto.ServiceRequestNotification;
import com.nocmok.orp.orp_solver.service.notification.mapper.ServiceRequestNotificationMapper;
import com.nocmok.orp.orp_solver.storage.notification.OrpOutputOutboxStorage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class ServiceRequestNotificationService {

    private ServiceRequestNotificationMapper serviceRequestNotificationMapper;
    private OrpOutputOutboxStorage orpOutputOutboxStorage;

    @Autowired
    public ServiceRequestNotificationService(
            ServiceRequestNotificationMapper serviceRequestNotificationMapper,
            OrpOutputOutboxStorage orpOutputOutboxStorage) {
        this.serviceRequestNotificationMapper = serviceRequestNotificationMapper;
        this.orpOutputOutboxStorage = orpOutputOutboxStorage;
    }

    public void sendNotification(ServiceRequestNotification serviceRequestNotification) {
        var record = serviceRequestNotificationMapper.mapServiceRequestNotificationToOrpOutboxRecord(serviceRequestNotification);
        log.info("notification scheduled " + record.getPayload());
        orpOutputOutboxStorage.insertOneRecord(record);
    }
}
