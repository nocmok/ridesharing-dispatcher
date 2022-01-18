package com.nocmok.orp.proto.solver;

import lombok.Getter;

import java.util.Collections;
import java.util.List;

@Getter
public class Matching {

    // The who serve the request
    private Vehicle servingVehicle;

    // If request cannot be processed
    // this value holds reason why this happened
    private DenialReason denialReason = DenialReason.ACCEPTED;

    // Скорректированный маршрут тс с учетом нового запроса
    private List<Integer> augmentedRoute;

    // Суммарная дистанция построенного маршрута
    private double augmentedRouteDistance;

    public Matching(Vehicle servingVehicle, List<Integer> augmentedRoute, double augmentedRouteDistance) {
        this.servingVehicle = servingVehicle;
        this.augmentedRoute = augmentedRoute;
        this.augmentedRouteDistance = augmentedRouteDistance;
    }

    public Matching(DenialReason denialReason) {
        this.servingVehicle = null;
        this.augmentedRoute = Collections.emptyList();
        this.augmentedRouteDistance = Double.POSITIVE_INFINITY;
        this.denialReason = denialReason;
    }

    public enum DenialReason {
        // Специальное значение обозначающее, что запрос удовлетворен
        ACCEPTED,
        NO_VEHICLE_NEARBY,
        OUT_OF_SERVICE_REGION,
    }
}
