package com.nocmok.orp.api.service.billing;

public interface BillingService {

    Double getDiscountInMeters(String orderId);

    Double getMetersToPayBeforeDiscount(String orderId);
}
