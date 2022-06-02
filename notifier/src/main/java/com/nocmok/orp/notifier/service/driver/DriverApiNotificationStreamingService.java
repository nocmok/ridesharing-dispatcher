package com.nocmok.orp.notifier.service.driver;

import com.nocmok.orp.kafka.orp_output.AssignRequestNotification;

public interface DriverApiNotificationStreamingService {

    void sendAssignRequestNotification(AssignRequestNotification assignRequestNotification);
}
