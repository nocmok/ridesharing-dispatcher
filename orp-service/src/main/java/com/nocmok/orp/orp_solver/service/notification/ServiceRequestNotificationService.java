package com.nocmok.orp.orp_solver.service.notification;

import com.nocmok.orp.orp_solver.storage.notification.ServiceRequestOutboxStorage;

public class ServiceRequestNotificationService {

    private final ServiceRequestNotificationMapper serviceRequestNotificationMapper = new ServiceRequestNotificationMapper();
    private ServiceRequestOutboxStorage serviceRequestOutboxStorage;

    public ServiceRequestNotificationService(ServiceRequestOutboxStorage serviceRequestOutboxStorage) {
        this.serviceRequestOutboxStorage = serviceRequestOutboxStorage;
    }

    public void sendNotification(ServiceRequestNotificationDto serviceRequestNotification) {
        serviceRequestOutboxStorage.insertOne(serviceRequestNotificationMapper.mapToServiceRequestOutboxEntry(serviceRequestNotification));
    }
}
