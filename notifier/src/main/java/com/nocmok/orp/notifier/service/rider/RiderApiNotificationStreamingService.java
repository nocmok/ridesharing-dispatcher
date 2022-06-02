package com.nocmok.orp.notifier.service.rider;

public interface RiderApiNotificationStreamingService {

    void sendOrderStatusChangedNotification(OrderStatusUpdatedNotification notification);
}
