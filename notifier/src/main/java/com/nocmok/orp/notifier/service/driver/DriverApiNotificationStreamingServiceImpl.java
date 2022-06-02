package com.nocmok.orp.notifier.service.driver;

import com.nocmok.orp.kafka.orp_output.AssignRequestNotification;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

@Component
public class DriverApiNotificationStreamingServiceImpl implements DriverApiNotificationStreamingService {

    private SimpMessagingTemplate messagingTemplate;

    @Autowired
    public DriverApiNotificationStreamingServiceImpl(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    private String getWebSocketTopicBySessionId(String sessionId) {
        return "/topic/driver/assign_request/" + sessionId;
    }

    @Override public void sendAssignRequestNotification(AssignRequestNotification assignRequestNotification) {
        messagingTemplate.convertAndSend(getWebSocketTopicBySessionId(assignRequestNotification.getSessionId()), assignRequestNotification);
    }
}
