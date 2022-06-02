package com.nocmok.orp.postgres.storage.dto;

public enum OrderStatus {

    SERVICE_PENDING,
    SERVICE_DENIED,
    ACCEPTED,
    PICKUP_PENDING,
    SERVING,
    SERVED,
    CANCELLED
}