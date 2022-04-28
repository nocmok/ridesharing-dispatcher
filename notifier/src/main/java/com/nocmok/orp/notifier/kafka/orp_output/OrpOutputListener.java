package com.nocmok.orp.notifier.kafka.orp_output;

import com.nocmok.orp.kafka.orp_output.AssignRequestNotification;
import com.nocmok.orp.notifier.service.driver.DriverApiNotificationStreamingService;
import com.nocmok.orp.notifier.service.rider.OrderStatusUpdatedNotification;
import com.nocmok.orp.notifier.service.rider.RiderApiNotificationStreamingService;
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

    private DriverApiNotificationStreamingService driverApiNotificationStreamingService;
    private RiderApiNotificationStreamingService riderApiNotificationStreamingService;

    @Autowired
    public OrpOutputListener(DriverApiNotificationStreamingService driverApiNotificationStreamingService,
                             RiderApiNotificationStreamingService riderApiNotificationStreamingService) {
        this.driverApiNotificationStreamingService = driverApiNotificationStreamingService;
        this.riderApiNotificationStreamingService = riderApiNotificationStreamingService;
    }

    @KafkaHandler
    public void receiveAssignRequestNotification(@Payload AssignRequestNotification message) {
//        log.info("received message " + message);
        driverApiNotificationStreamingService.sendAssignRequestNotification(message);
        riderApiNotificationStreamingService.sendOrderStatusChangedNotification(OrderStatusUpdatedNotification.builder()
                .orderId(message.getServiceRequestId())
                .build());
    }

    @KafkaHandler(isDefault = true)
    public void fallbackHandler(@Payload Object unknownMessage) {
        log.warn("message with unknown format received, will skip it: " + unknownMessage);
    }
}
