package com.nocmok.orp.orp_solver.service.notification;

import com.nocmok.orp.orp_solver.storage.notification.ServiceRequestOutboxEntry;

public class ServiceRequestNotificationMapper {

    public ServiceRequestOutboxEntry mapToServiceRequestOutboxEntry(ServiceRequestNotificationDto serviceRequestNotificationDto) {
        return ServiceRequestOutboxEntry.builder()
                .vehicleId(serviceRequestNotificationDto.getVehicleId())
                .requestId(serviceRequestNotificationDto.getRequestId())
                .reservationId(serviceRequestNotificationDto.getReservationId())
                .build();
    }
}
