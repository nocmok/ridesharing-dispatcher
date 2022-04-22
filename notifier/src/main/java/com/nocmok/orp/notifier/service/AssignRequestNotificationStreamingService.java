package com.nocmok.orp.notifier.service;

import com.nocmok.orp.kafka.orp_output.AssignRequestNotification;

public interface AssignRequestNotificationStreamingService {

    void sendNotification(AssignRequestNotification assignRequestNotification);
}
