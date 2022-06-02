package com.nocmok.orp.api.controller.billing_api;

import com.nocmok.orp.api.controller.billing_api.dto.GetBillingRequest;
import com.nocmok.orp.api.controller.billing_api.dto.GetBillingResponse;
import com.nocmok.orp.api.service.billing.BillingService;
import com.nocmok.orp.api.service.request.RequestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("order_api/v0/billing")
public class BillingApi {

    private BillingService billingService;
    private RequestService requestService;

    @Autowired
    public BillingApi(BillingService billingService, RequestService requestService) {
        this.billingService = billingService;
        this.requestService = requestService;
    }

    @PostMapping
    public ResponseEntity<GetBillingResponse> getBilling(@RequestBody GetBillingRequest request) {
        if (requestService.getRequestInfo(request.getOrderId()).isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return new ResponseEntity<>(GetBillingResponse.builder()
                .orderId(request.getOrderId())
                .discountMeters(billingService.getDiscountInMeters(request.getOrderId()))
                .metersToPayBeforeDiscount(billingService.getMetersToPayBeforeDiscount(request.getOrderId()))
                .build(), HttpStatus.OK);
    }
}
