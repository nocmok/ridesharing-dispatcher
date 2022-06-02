package com.nocmok.orp.api.service.request;

import com.nocmok.orp.api.service.session.SessionStatisticsService;
import com.nocmok.orp.postgres.storage.ServiceRequestStorage;
import com.nocmok.orp.postgres.storage.dto.OrderStatus;
import com.nocmok.orp.postgres.storage.dto.ServiceRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
public class OrderStatisticsServiceImpl implements OrderStatisticsService {

    private ServiceRequestStorage serviceRequestStorage;
    private SessionStatisticsService sessionStatisticsService;

    @Autowired
    public OrderStatisticsServiceImpl(ServiceRequestStorage serviceRequestStorage,
                                      SessionStatisticsService sessionStatisticsService) {
        this.serviceRequestStorage = serviceRequestStorage;
        this.sessionStatisticsService = sessionStatisticsService;
    }

    private Long getTotalTimeByStatusLog(List<ServiceRequest.OrderStatusLogEntry> statusLog) {
        return statusLog.get(statusLog.size() - 1).getUpdatedAt().getEpochSecond() - statusLog.get(0).getUpdatedAt().getEpochSecond();
    }

    private Long getTimeByStatusLogAndStatus(List<ServiceRequest.OrderStatusLogEntry> statusLog, OrderStatus status) {
        var it = statusLog.iterator();
        var prevStatusLogEntry = it.next();
        long totalTime = 0;
        while (it.hasNext()) {
            var nextStatusLogEntry = it.next();
            if (prevStatusLogEntry.getOrderStatus() == status) {
                totalTime += nextStatusLogEntry.getUpdatedAt().getEpochSecond() - prevStatusLogEntry.getUpdatedAt().getEpochSecond();
            }
            prevStatusLogEntry = nextStatusLogEntry;
        }
        if (prevStatusLogEntry.getOrderStatus() == status) {
            totalTime += Instant.now().getEpochSecond() - prevStatusLogEntry.getUpdatedAt().getEpochSecond();
        }
        return totalTime;
    }

    private Double getTravelledDistanceByStatusLog(ServiceRequest order, List<ServiceRequest.OrderStatusLogEntry> statusLog) {
        var pickupTimeOptional = statusLog.stream()
                .filter(orderStatusLogEntry -> orderStatusLogEntry.getOrderStatus() == OrderStatus.SERVING)
                .findFirst()
                .map(ServiceRequest.OrderStatusLogEntry::getUpdatedAt);
        if (pickupTimeOptional.isEmpty()) {
            return 0d;
        }
        var pickupTime = pickupTimeOptional.get();
        var dropOffTime = statusLog.stream()
                .filter(orderStatusLogEntry -> orderStatusLogEntry.getOrderStatus() == OrderStatus.SERVED)
                .findFirst()
                .map(ServiceRequest.OrderStatusLogEntry::getUpdatedAt)
                .orElse(Instant.now());
        return sessionStatisticsService.getDistanceTravelledBySessionWithinTimeInterval(order.getServingSessionId(), pickupTime, dropOffTime);
    }

    private Double getServiceDistanceByStatusLog(ServiceRequest order, List<ServiceRequest.OrderStatusLogEntry> statusLog) {
        var acceptTimeOptional = statusLog.stream()
                .filter(orderStatusLogEntry -> orderStatusLogEntry.getOrderStatus() == OrderStatus.ACCEPTED)
                .findFirst()
                .map(ServiceRequest.OrderStatusLogEntry::getUpdatedAt);
        if (acceptTimeOptional.isEmpty()) {
            return 0d;
        }
        var acceptTime = acceptTimeOptional.get();
        var dropOffTime = statusLog.stream()
                .filter(orderStatusLogEntry -> orderStatusLogEntry.getOrderStatus() == OrderStatus.SERVED)
                .findFirst()
                .map(ServiceRequest.OrderStatusLogEntry::getUpdatedAt)
                .orElse(Instant.now());
        return sessionStatisticsService.getDistanceTravelledBySessionWithinTimeInterval(order.getServingSessionId(), acceptTime, dropOffTime);
    }

    @Override public Optional<OrderStatistics> getOrderStatisticsSummary(String orderId) {
        var orderOptional = serviceRequestStorage.getRequestById(orderId);
        if (orderOptional.isEmpty()) {
            return Optional.empty();
        }
        var order = orderOptional.get();
        var statusLog = serviceRequestStorage.getOrderStatusLog(Long.parseLong(orderId), 0, Integer.MAX_VALUE, true);
        if (statusLog.isEmpty()) {
            log.warn("order " + orderId + " has empty status log");
            return Optional.empty();
        }
        return Optional.of(OrderStatistics.builder()
                .totalTime(getTotalTimeByStatusLog(statusLog))
                .dispatchWaitingTime(getTimeByStatusLogAndStatus(statusLog, OrderStatus.SERVICE_PENDING))
                .serviceWaitingTime(getTimeByStatusLogAndStatus(statusLog, OrderStatus.ACCEPTED))
                .pickupWaitingTime(getTimeByStatusLogAndStatus(statusLog, OrderStatus.PICKUP_PENDING))
                .travelTime(getTimeByStatusLogAndStatus(statusLog, OrderStatus.SERVING))
                .distanceTravelled(getTravelledDistanceByStatusLog(order, statusLog))
                .serviceDistance(getServiceDistanceByStatusLog(order, statusLog))
                .build());
    }

    private List<Integer> getCompanionsHistoryByStatusLog(List<OrderStatusLogEntryWithId> statusLog) {
        int nCompanions = 0;
        var result = new ArrayList<Integer>();
        for (int i = 1; i < statusLog.size(); ++i) {
            var status = statusLog.get(i - 1).status();
            if (status == OrderStatus.SERVING) {
                ++nCompanions;
            } else if (status == OrderStatus.SERVED) {
                --nCompanions;
            }
            result.add(nCompanions);
        }
        return result;
    }

    private List<Integer> getCompanionOrdersHistoryByStatusLog(List<OrderStatusLogEntryWithId> statusLog) {
        int nCompanionOrders = 0;
        var result = new ArrayList<Integer>();
        for (int i = 1; i < statusLog.size(); ++i) {
            var status = statusLog.get(i - 1).status();
            if (status == OrderStatus.ACCEPTED) {
                ++nCompanionOrders;
            } else if (status == OrderStatus.CANCELLED || status == OrderStatus.SERVED) {
                --nCompanionOrders;
            }
            result.add(nCompanionOrders);
        }
        return result;
    }

    private List<OrderStatus> getOrderStatusHistoryByStatusLog(Long orderId, List<OrderStatusLogEntryWithId> statusLog) {
        OrderStatus status = null;
        var result = new ArrayList<OrderStatus>();
        for (int i = 1; i < statusLog.size(); ++i) {
            if (Objects.equals(orderId, statusLog.get(i - 1).orderId())) {
                status = statusLog.get(i - 1).status();
            }
            result.add(status);
        }
        return result;
    }

    private List<Double> getDistanceHistoryByStatusLog(List<OrderStatusLogEntryWithId> statusLog) {
        if (statusLog.isEmpty()) {
            return Collections.emptyList();
        }
        var servingSessionId = serviceRequestStorage.getRequestById(statusLog.get(0).orderId().toString())
                .map(ServiceRequest::getServingSessionId)
                .orElseThrow(() -> new RuntimeException("order with id " + statusLog.get(0).orderId() + " doesn't exist"));
        var result = new ArrayList<Double>();
        for (int i = 1; i < statusLog.size(); ++i) {
            Instant from = statusLog.get(i - 1).updatedAt();
            Instant to = statusLog.get(i).updatedAt();
            result.add(sessionStatisticsService.getDistanceTravelledBySessionWithinTimeInterval(servingSessionId, from, to));
        }
        return result;
    }

    private Optional<Instant> getOrderAcceptedTime(List<ServiceRequest.OrderStatusLogEntry> statusLog) {
        return statusLog.stream()
                .filter(orderStatusLogEntry -> orderStatusLogEntry.getOrderStatus() == OrderStatus.ACCEPTED)
                .map(ServiceRequest.OrderStatusLogEntry::getUpdatedAt)
                .findFirst();
    }

    private Optional<Instant> getOrderServedTime(List<ServiceRequest.OrderStatusLogEntry> statusLog) {
        return statusLog.stream()
                .filter(orderStatusLogEntry -> orderStatusLogEntry.getOrderStatus() == OrderStatus.SERVED)
                .map(ServiceRequest.OrderStatusLogEntry::getUpdatedAt)
                .findFirst();
    }

    private List<OrderHistoryInterval> getOrderHistory(ServiceRequest order) {
        var statusLog = serviceRequestStorage.getOrderStatusLog(Long.parseLong(order.getRequestId()), 0, Integer.MAX_VALUE, true);
        var startTime = getOrderAcceptedTime(statusLog).orElse(null);
        if (startTime == null) {
            return Collections.emptyList();
        }
        var endTime = getOrderServedTime(statusLog).orElse(Instant.now());

        var companionOrdersIds =
                serviceRequestStorage.getSessionActiveOrdersInsideTimeInterval(Long.parseLong(order.getServingSessionId()), startTime, endTime);

        var mergedStatusLog = serviceRequestStorage.getOrdersStatusLogs(companionOrdersIds).entrySet().stream()
                .flatMap(entry -> entry.getValue().stream().map(orderStatusLogEntry -> new OrderStatusLogEntryWithId(entry.getKey(), orderStatusLogEntry)))
                .filter(entry -> entry.status() != OrderStatus.SERVICE_PENDING && entry.status() != OrderStatus.PICKUP_PENDING)
                .sorted()
                .collect(Collectors.toCollection(ArrayList::new));

        var companionsHistory = getCompanionsHistoryByStatusLog(mergedStatusLog);
        var companionOrdersHistory = getCompanionOrdersHistoryByStatusLog(mergedStatusLog);
        var orderStatusesHistory = getOrderStatusHistoryByStatusLog(Long.parseLong(order.getRequestId()), mergedStatusLog);
        var distancesHistory = getDistanceHistoryByStatusLog(mergedStatusLog);

        var result = new ArrayList<OrderHistoryInterval>();

        for (int i = 1; i < mergedStatusLog.size(); ++i) {
            if (mergedStatusLog.get(i - 1).updatedAt().compareTo(startTime) < 0) {
                continue;
            }
            if (mergedStatusLog.get(i - 1).updatedAt().compareTo(endTime) >= 0) {
                break;
            }
            result.add(OrderHistoryInterval.builder()
                    .startTime(mergedStatusLog.get(i - 1).updatedAt())
                    .endTime(mergedStatusLog.get(i).updatedAt())
                    .companions(companionsHistory.get(i - 1))
                    .companionOrders(companionOrdersHistory.get(i - 1))
                    .distance(distancesHistory.get(i - 1))
                    .status(orderStatusesHistory.get(i - 1))
                    .build());
        }

        return result;
    }

    @Override public List<OrderHistoryInterval> getOrderHistory(String orderId) {
        return serviceRequestStorage.getRequestById(orderId)
                .map(this::getOrderHistory)
                .orElse(Collections.emptyList());
    }

    private static final class OrderStatusLogEntryWithId implements Comparable<OrderStatusLogEntryWithId> {
        private Long orderId;
        private ServiceRequest.OrderStatusLogEntry orderStatusLogEntry;

        public OrderStatusLogEntryWithId(Long orderId, ServiceRequest.OrderStatusLogEntry entry) {
            this.orderId = orderId;
            this.orderStatusLogEntry = entry;
        }

        public Long orderId() {
            return orderId;
        }

        public OrderStatus status() {
            return orderStatusLogEntry.getOrderStatus();
        }

        public Instant updatedAt() {
            return orderStatusLogEntry.getUpdatedAt();
        }

        @Override public int compareTo(OrderStatusLogEntryWithId o) {
            return Long.compare(updatedAt().toEpochMilli(), o.updatedAt().toEpochMilli());
        }
    }
}
