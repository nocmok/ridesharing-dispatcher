package com.nocmok.orp.solver.ls;

import com.nocmok.orp.graph.api.Segment;
import com.nocmok.orp.solver.api.ScheduleNode;
import com.nocmok.orp.solver.api.ScheduleNodeKind;
import com.nocmok.orp.state_keeper.api.ScheduleEntry;
import com.nocmok.orp.state_keeper.api.ScheduleEntryKind;
import com.nocmok.orp.state_keeper.api.VehicleState;
import com.nocmok.orp.state_keeper.api.VehicleStatus;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.nocmok.orp.solver.api.ScheduleNodeKind.DROPOFF;
import static com.nocmok.orp.solver.api.ScheduleNodeKind.PICKUP;

/**
 * Обертка над интерфейсом тс, обогащенная геоданными
 */
class ExtendedVehicle {

    private final String id;
    private final VehicleStatus status;
    private final List<ScheduleNode> schedule;
    private final NodesRoute routeScheduled;
    private final Integer capacity;
    private final Integer residualCapacity;
    private final Double latitude;
    private final Double longitude;
    private final Segment roadSegment;
    private final Double progressOnRoadSegment;

    public ExtendedVehicle(VehicleState state, Double latitude, Double longitude, Segment roadSegment, Double progressOnRoadSegment,
                           NodesRoute routeScheduled) {
        this.id = state.getId();
        this.status = state.getStatus();
        this.schedule = state.getSchedule().stream()
                .map(this::mapScheduleEntryToScheduleNode)
                .collect(Collectors.toCollection(ArrayList::new));
//        this.routeScheduled = state.getRouteScheduled().stream()
//                .map(this::mapRouteEntryToRouteNode)
//                .collect(Collectors.toCollection(ArrayList::new));
        this.routeScheduled = routeScheduled;
        this.capacity = state.getCapacity();
        this.residualCapacity = state.getResidualCapacity();
        this.latitude = latitude;
        this.longitude = longitude;
        this.roadSegment = roadSegment;
        this.progressOnRoadSegment = progressOnRoadSegment;
    }

    private ScheduleNodeKind mapScheduleEntryKindToScheduleNodeKind(ScheduleEntryKind scheduleEntryKind) {
        switch (scheduleEntryKind) {
            case PICKUP:
                return PICKUP;
            case DROPOFF:
                return DROPOFF;
            default:
                throw new IllegalArgumentException("unknown schedule entry kind: " + scheduleEntryKind);
        }
    }

    private ScheduleNode mapScheduleEntryToScheduleNode(ScheduleEntry scheduleEntry) {
        return new ScheduleNode(
                scheduleEntry.getDeadline(),
                scheduleEntry.getLoad(),
                scheduleEntry.getNodeId(),
                scheduleEntry.getLatitude(),
                scheduleEntry.getLongitude(),
                mapScheduleEntryKindToScheduleNodeKind(scheduleEntry.getKind()),
                scheduleEntry.getOrderId()
        );
    }

    public String getId() {
        return id;
    }

    public VehicleStatus getStatus() {
        return status;
    }

    public List<ScheduleNode> getSchedule() {
        return schedule;
    }

    public NodesRoute getRouteScheduled() {
        return routeScheduled;
    }

    public Integer getCapacity() {
        return capacity;
    }

    public Integer getResidualCapacity() {
        return residualCapacity;
    }

    public Double getCostToNextNodeInScheduledRoute() {
        return roadSegment.getCost() * (1 - progressOnRoadSegment);
    }

    public Double getLatitude() {
        return latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public Segment getRoadSegment() {
        return roadSegment;
    }

    /**
     * Число в диапазоне от 0 до 1, где
     * 0 соответствует положению транспортного средства в начальной точке дороги,
     * 1 соответствует положению транспортного средства в конечной точке дороги.
     * Промежуточные значения примерно отражают степень прохождения дороги транспортным средством,
     * например 0.5 означает, что транспортное средство преодолело половину дорожного участка
     */
    public Double getProgressOnRoadSegment() {
        return progressOnRoadSegment;
    }
}
