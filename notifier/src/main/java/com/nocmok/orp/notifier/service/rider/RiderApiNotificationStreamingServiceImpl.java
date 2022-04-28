package com.nocmok.orp.notifier.service.rider;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class RiderApiNotificationStreamingServiceImpl implements RiderApiNotificationStreamingService {

    private SimpMessagingTemplate messagingTemplate;

    @Autowired
    public RiderApiNotificationStreamingServiceImpl(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    @Override public void sendOrderStatusChangedNotification(OrderStatusUpdatedNotification notification) {
        log.info("send notification " + notification + ", to " + "/topic/rider/order_status/" + notification.getOrderId());
        messagingTemplate.convertAndSend("/topic/rider/order_status/" + notification.getOrderId(), notification);
    }
}
