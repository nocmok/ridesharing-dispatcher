package com.nocmok.orp.api.service.billing;

import com.nocmok.orp.api.service.request.OrderHistoryInterval;
import com.nocmok.orp.api.service.request.OrderStatistics;
import com.nocmok.orp.api.service.request.OrderStatisticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BillingServiceImpl implements BillingService {

    private OrderStatisticsService orderStatisticsService;

    @Autowired
    public BillingServiceImpl(OrderStatisticsService orderStatisticsService) {
        this.orderStatisticsService = orderStatisticsService;
    }

    private Double computeSavedDistanceByOrderHistory(List<OrderHistoryInterval> history) {
        return history.stream().map(orderHistoryInterval -> {
            if (orderHistoryInterval.getCompanionOrders() < 1) {
                return 0d;
            }
            double distanceSavings = orderHistoryInterval.getDistance() * (orderHistoryInterval.getCompanionOrders() - 1);
            return distanceSavings / orderHistoryInterval.getCompanionOrders();
        }).reduce(0d, Double::sum);
    }

    @Override public Double getDiscountInMeters(String orderId) {
        var orderHistory = orderStatisticsService.getOrderHistory(orderId);
        return computeSavedDistanceByOrderHistory(orderHistory);
    }

    @Override public Double getMetersToPayBeforeDiscount(String orderId) {
        return orderStatisticsService.getOrderStatisticsSummary(orderId)
                .map(OrderStatistics::getServiceDistance)
                .orElse(null);
    }
}
