package com.nocmok.orp.notifier.kafka.orp_output;

import com.nocmok.orp.kafka.orp_output.AssignRequestNotification;
import com.nocmok.orp.kafka.orp_telemetry.VehicleTelemetryMessage;
import com.nocmok.orp.notifier.kafka.orp_telemetry.mapper.VehicleTelemetryMapper;
import com.nocmok.orp.notifier.service.AssignRequestNotificationStreamingService;
import com.nocmok.orp.notifier.service.VehicleGPSStreamingService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Component
@KafkaListener(
        topics = "orp.output",
        groupId = "orp_notifier",
        containerFactory = "orpOutputKafkaListenerContainerFactory")
@Slf4j
public class OrpOutputListener {

    private AssignRequestNotificationStreamingService assignRequestNotificationStreamingService;

    @Autowired
    public OrpOutputListener(AssignRequestNotificationStreamingService assignRequestNotificationStreamingService) {
        this.assignRequestNotificationStreamingService = assignRequestNotificationStreamingService;
    }

    @KafkaHandler
    public void receiveAssignRequestNotification(@Payload AssignRequestNotification message) {
//        log.info("received message " + message);
        assignRequestNotificationStreamingService.sendNotification(message);
    }

    @KafkaHandler(isDefault = true)
    public void fallbackHandler(@Payload Object unknownMessage) {
        log.warn("message with unknown format received, will skip it: " + unknownMessage);
    }
}
