package com.nocmok.orp.api.service.request;

import java.util.List;
import java.util.Optional;

public interface OrderStatisticsService {

    Optional<OrderStatistics> getOrderStatisticsSummary(String orderId);

    List<OrderHistoryInterval> getOrderHistory(String orderId);
}
