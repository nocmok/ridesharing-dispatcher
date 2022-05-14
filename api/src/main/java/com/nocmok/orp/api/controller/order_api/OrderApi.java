package com.nocmok.orp.api.controller.order_api;

import com.nocmok.orp.api.controller.order_api.dto.GetOrderStatusLogRequest;
import com.nocmok.orp.api.controller.order_api.dto.GetOrderStatusLogResponse;
import com.nocmok.orp.api.controller.order_api.dto.OrderStatusLogEntry;
import com.nocmok.orp.api.service.request_management.RequestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.stream.Collectors;

@Controller
@RequestMapping("/order_api/v0")
public class OrderApi {

    private RequestService requestService;

    @Autowired
    public OrderApi(RequestService requestService) {
        this.requestService = requestService;
    }

    @PostMapping("/statistic/status_log")
    public @ResponseBody
    GetOrderStatusLogResponse getOrderStatusLog(@RequestBody GetOrderStatusLogRequest request) {
        var statusLog = requestService.getOrderStatusLog(request.getOrderId(), request.getPage(), request.getEntriesPerPage(), request.getAscending());
        return GetOrderStatusLogResponse.builder()
                .orderId(request.getOrderId())
                .statusLog(statusLog.stream()
                        .map(orderStatusLogEntry -> OrderStatusLogEntry.builder()
                                .status(orderStatusLogEntry.getOrderStatus())
                                .updatedAt(orderStatusLogEntry.getUpdatedAt())
                                .build())
                        .collect(Collectors.toList()))
                .build();
    }
}
