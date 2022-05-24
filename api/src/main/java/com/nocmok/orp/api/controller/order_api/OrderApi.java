package com.nocmok.orp.api.controller.order_api;

import com.nocmok.orp.api.controller.common_dto.Coordinates;
import com.nocmok.orp.api.controller.common_dto.RequestInfo;
import com.nocmok.orp.api.controller.common_dto.RoadSegment;
import com.nocmok.orp.api.controller.order_api.dto.GetOrderExpendituresRequest;
import com.nocmok.orp.api.controller.order_api.dto.GetOrderExpendituresResponse;
import com.nocmok.orp.api.controller.order_api.dto.GetOrderHistoryRequest;
import com.nocmok.orp.api.controller.order_api.dto.GetOrderHistoryResponse;
import com.nocmok.orp.api.controller.order_api.dto.GetOrderStatusLogRequest;
import com.nocmok.orp.api.controller.order_api.dto.GetOrderStatusLogResponse;
import com.nocmok.orp.api.controller.order_api.dto.GetOrdersRequest;
import com.nocmok.orp.api.controller.order_api.dto.GetOrdersResponse;
import com.nocmok.orp.api.controller.order_api.dto.OrderExecutionInterval;
import com.nocmok.orp.api.controller.order_api.dto.OrderExpendituresSummary;
import com.nocmok.orp.api.controller.order_api.dto.OrderStatusLogEntry;
import com.nocmok.orp.api.controller.order_api.mapper.FilterMapper;
import com.nocmok.orp.api.service.request.OrderStatisticsService;
import com.nocmok.orp.api.service.request.RequestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
    private OrderStatisticsService orderStatisticsService;
    private FilterMapper filterMapper;

    @Autowired
    public OrderApi(RequestService requestService, OrderStatisticsService orderStatisticsService,
                    FilterMapper filterMapper) {
        this.requestService = requestService;
        this.orderStatisticsService = orderStatisticsService;
        this.filterMapper = filterMapper;
    }

    @PostMapping("/statistics/status_log")
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

    @PostMapping("/statistics/expenditures")
    public @ResponseBody
    ResponseEntity<GetOrderExpendituresResponse> getOrderExpenditures(@RequestBody GetOrderExpendituresRequest request) {
        return orderStatisticsService.getOrderStatisticsSummary(request.getOrderId())
                .map(summary -> new ResponseEntity<>(
                        GetOrderExpendituresResponse.builder()
                                .orderId(request.getOrderId())
                                .summary(
                                        OrderExpendituresSummary.builder()
                                                .distanceScheduled(summary.getDistanceScheduled())
                                                .distanceTravelled(summary.getDistanceTravelled())
                                                .combinedDistance(summary.getCombinedDistance())
                                                .dispatchWaitingTime(summary.getDispatchWaitingTime())
                                                .serviceWaitingTime(summary.getServiceWaitingTime())
                                                .pickupWaitingTime(summary.getPickupWaitingTime())
                                                .travelTime(summary.getTravelTime())
                                                .totalTime(summary.getTotalTime())
                                                .build())
                                .build(),
                        HttpStatus.OK))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping("/statistics/history")
    public @ResponseBody
    GetOrderHistoryResponse getOrderHistory(@RequestBody GetOrderHistoryRequest request) {
        var orderHistory = orderStatisticsService.getOrderHistory(request.getOrderId());
        return GetOrderHistoryResponse.builder()
                .orderId(request.getOrderId())
                .history(orderHistory.stream()
                        .map(entry -> OrderExecutionInterval.builder()
                                .startTime(entry.getStartTime())
                                .endTime(entry.getEndTime())
                                .companions(entry.getCompanions())
                                .companionOrders(entry.getCompanionOrders())
                                .distance(entry.getDistance())
                                .status(entry.getStatus().name())
                                .build())
                        .collect(Collectors.toList()))
                .build();
    }

    @PostMapping("/orders")
    public @ResponseBody
    GetOrdersResponse getOrders(@RequestBody GetOrdersRequest request) {
        var filter = filterMapper.mapRequestFilterToInternalFilter(request.getFilter());
        var orders = requestService.getOrders(filter).stream().map(order -> RequestInfo.builder()
                .requestId(order.getRequestId())
                .recordedOrigin(new Coordinates(order.getRecordedOriginLatitude(), order.getRecordedOriginLongitude()))
                .recordedDestination(
                        new Coordinates(order.getRecordedDestinationLatitude(), order.getRecordedDestinationLongitude()))
                .pickupRoadSegment(new RoadSegment(order.getPickupRoadSegmentStartNodeId(), order.getPickupRoadSegmentEndNodeId()))
                .dropoffRoadSegment(new RoadSegment(order.getDropOffRoadSegmentStartNodeId(), order.getDropOffRoadSegmentEndNodeId()))
                .detourConstraint(order.getDetourConstraint())
                .load(order.getLoad())
                .maxPickupDelaySeconds(order.getMaxPickupDelaySeconds())
                .requestedAt(order.getRequestedAt())
                .completedAt(order.getCompletedAt())
                .status(order.getStatus())
                .servingSessionId(order.getServingSessionId())
                .build()).collect(Collectors.toList());
        return GetOrdersResponse.builder()
                .orders(orders)
                .build();
    }
}
