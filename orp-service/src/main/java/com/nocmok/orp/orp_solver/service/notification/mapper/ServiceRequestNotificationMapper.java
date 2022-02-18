package com.nocmok.orp.orp_solver.service.notification.mapper;

import com.nocmok.orp.orp_solver.service.notification.dto.ServiceRequestNotification;
import com.nocmok.orp.orp_solver.storage.notification.ServiceRequestOutboxEntry;

public class ServiceRequestNotificationMapper {

    public ServiceRequestOutboxEntry mapToServiceRequestOutboxEntry(ServiceRequestNotification serviceRequestNotificationDto) {
        return ServiceRequestOutboxEntry.builder()
                .vehicleId(serviceRequestNotificationDto.getVehicleId())
                .requestId(serviceRequestNotificationDto.getRequestId())
                .reservationId(serviceRequestNotificationDto.getReservationId())
                .build();
    }
}
