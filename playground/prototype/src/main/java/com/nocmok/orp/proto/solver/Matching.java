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
    private Route route;

    private List<ScheduleCheckpoint> schedule;

    public Matching(Vehicle servingVehicle, Route route, List<ScheduleCheckpoint> schedule) {
        this.servingVehicle = servingVehicle;
        this.route = route;
        this.schedule = schedule;
    }

    public Matching(DenialReason denialReason) {
        this.servingVehicle = null;
        this.route = new Route(Collections.emptyList(), Double.POSITIVE_INFINITY);
        this.schedule = Collections.emptyList();
        this.denialReason = denialReason;
    }

    public enum DenialReason {
        // Специальное значение обозначающее, что запрос удовлетворен
        ACCEPTED,
        NO_VEHICLE_NEARBY,
        OUT_OF_SERVICE_REGION,
    }
}
