package com.nocmok.orp.proto.simulator;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Metrics {

    // Суммарное расстояние, пройденное тс
    private double totalTravelledDistance;

    // Общее количество поступивших запросов
    private int totalRequests;

    // Общее количество отклоненных запросов
    private int deniedRequests;

    // Сумма кратчайших расстояний от точки отправления до точки прибытия для всех выполненных запросов
    private double acceptedRequestsEffectiveDistance;

    // Сумма кратчайших расстояний от точки отправления до точки прибытия для всех отклоненных запросов
    private double deniedRequestsEffectiveDistance;

    // Сумма кратчайших расстояний от точки отправления до точки прибытия для всех запросов
    private double effectiveDistance;

    // Общее расстояние пройденное тс в состоянии SERVING
    private double travelledEffectiveDistance;

    // Общее время работы алгоритма в миллисекундах
    private long totalProcessingTime;

    // Минимальное значение времени обработки одного запроса в миллисекундах
    private long processingTimePerRequestMinimum = Long.MAX_VALUE;

    // Максимальное значение времени обработки одного запроса в миллисекундах
    private long processingTimePerRequestMaximum = 0;

    public double getDistanceSavings() {
        return 1d - (totalTravelledDistance + deniedRequestsEffectiveDistance) / effectiveDistance;
    }

    public double getDistanceSavingsMeters() {
        return effectiveDistance - totalTravelledDistance - deniedRequestsEffectiveDistance;
    }

    public double getCombinedDistance() {
        return acceptedRequestsEffectiveDistance - travelledEffectiveDistance;
    }

    public double getCombinedDistancePercentage() {
        return (acceptedRequestsEffectiveDistance - travelledEffectiveDistance) / acceptedRequestsEffectiveDistance;
    }

    public double getServiceRate() {
        return (double) (totalRequests - deniedRequests) / totalRequests;
    }

    // Среднее значение времени обработки одного запроса в миллисекундах
    public double getProcessingTimePerRequestExpectation() {
        return (double) totalProcessingTime / totalRequests;
    }

    @Override public String toString() {
        return "totalRequests=" + totalRequests +
                "\ndenied requests=" + deniedRequests +
                "\nservice rate (%)=" + getServiceRate() * 100 +
                "\ntravelled distance (m)=" + getTotalTravelledDistance() +
                "\ndistance savings (%)=" + getDistanceSavings() * 100 +
                "\ndistance savings (m)=" + getDistanceSavingsMeters() +
                "\ncombined distance (%)=" + getCombinedDistancePercentage() * 100 +
                "\ncombined distance (m)=" + getCombinedDistance() +
                "\naverage processing time (ms)=" + getProcessingTimePerRequestExpectation() +
                "\nminimum processing time (ms)=" + processingTimePerRequestMinimum +
                "\nmaximum processing time (ms)=" + processingTimePerRequestMaximum;

    }
}
