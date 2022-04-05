package com.nocmok.orp.notifier.service;

import com.nocmok.orp.notifier.service.dto.VehicleGPSMessageServiceDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
public class VehicleGPSStreamingServiceImpl implements VehicleGPSStreamingService {

    private SimpMessagingTemplate messagingTemplate;

    @Autowired
    public VehicleGPSStreamingServiceImpl(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    private boolean validateSessionId(String sessionId) {
        return sessionId.matches("[a-zA-Z0-9]+");
    }

    private String getWebSocketTopicBySessionId(String sessionId) {
        if (!validateSessionId(sessionId)) {
            throw new IllegalArgumentException("malformed session id " + sessionId);
        }
        return "/topic/telemetry/gps/" + sessionId;
    }

    @Override public void sendGPS(VehicleGPSMessageServiceDto gpsMessageDto) {
        messagingTemplate.convertAndSend(getWebSocketTopicBySessionId(gpsMessageDto.getSessionId()), gpsMessageDto);
    }
}
