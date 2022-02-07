package com.nocmok.orp.solver.ls;

import com.google.common.collect.MinMaxPriorityQueue;
import com.nocmok.orp.core_api.GCS;
import com.nocmok.orp.core_api.OrpSolver;
import com.nocmok.orp.core_api.Request;
import com.nocmok.orp.core_api.RoadIndex;
import com.nocmok.orp.core_api.RoadIndexEntity;
import com.nocmok.orp.core_api.RoadNode;
import com.nocmok.orp.core_api.RoadRoute;
import com.nocmok.orp.core_api.ScheduleNode;
import com.nocmok.orp.core_api.ScheduleNodeKind;
import com.nocmok.orp.core_api.Vehicle;
import com.nocmok.orp.core_api.VehicleStateService;
import com.nocmok.orp.core_api.VehicleStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class LSSolver implements OrpSolver {

    private static Logger log = LoggerFactory.getLogger(LSSolver.class);
    private RoadIndex roadIndex;
    private VehicleStateService<? extends Vehicle> vehicleStateService;

    public LSSolver(RoadIndex roadIndex, VehicleStateService<? extends Vehicle> vehicleStateService) {
        this.roadIndex = roadIndex;
        this.vehicleStateService = vehicleStateService;
    }

    /**
     * Отсекает тс, которые точно не могут обработать запрос
     */
    private List<? extends Vehicle> filterVehicles(Request request) {
        // TODO добавить двустороннюю фильтрацию
        long timeReserveSeconds = request.getRequestedAt().getEpochSecond() + request.getMaxPickupDelaySeconds() - Instant.now().getEpochSecond();
        List<String> filteredVehiclesId = roadIndex
                .getNeighborhood(new GCS(request.getPickupLat(), request.getPickupLon()), timeReserveSeconds)
                .stream()
                .map(RoadIndexEntity::getId)
                .collect(Collectors.toList());
        return vehicleStateService.getVehiclesByIds(filteredVehiclesId);
    }

    private ScheduleNode createPickupScheduleNode(Request request) {
        return new ScheduleNode(
                request.getRequestedAt().plusSeconds(request.getMaxPickupDelaySeconds()),
                request.getLoad(),
                request.getPickupNodeId(),
                request.getPickupLat(),
                request.getPickupLon(),
                ScheduleNodeKind.PICKUP,
                request.getRequestId()
        );
    }

    private ScheduleNode createDropoffScheduleNode(Request request) {
        Instant deadline = request.getRequestedAt()
                .plusSeconds(request.getMaxPickupDelaySeconds())
                .plusSeconds((long) (roadIndex.shortestRoute(
                        request.getPickupNodeId(),
                        request.getDropoffNodeId()).getCost() * request.getDetourConstraint()));
        return new ScheduleNode(
                deadline,
                request.getLoad(),
                request.getDropoffNodeId(),
                request.getDropoffLat(),
                request.getDropoffLon(),
                ScheduleNodeKind.DROPOFF,
                request.getRequestId()
        );
    }

    /**
     * Объединяет цепочку смежных по вершине маршрутов в один маршрут
     */
    private RoadRoute combineRoutes(List<RoadRoute> routes) {
        var combinedRoute = new ArrayList<RoadNode>();
        double combinedCost = 0;
        for (var route : routes) {
            if (!combinedRoute.isEmpty()) {
                combinedRoute.remove(combinedRoute.size() - 1);
            }
            combinedRoute.addAll(route.getRoute());
            combinedCost += route.getCost();
        }
        return new RoadRoute(combinedRoute, combinedCost);
    }

    /**
     * Принимает список идентификаторов вершин привязанных к контрольным точкам.
     * Возвращает маршрут, который обходит переданные точки в заданном порядке
     */
    private RoadRoute getRouteToCompleteSchedule(List<Integer> schedule) {
        var partialRoutes = new ArrayList<RoadRoute>();
        for (int i = 1; i < schedule.size(); ++i) {
            partialRoutes.add(roadIndex.shortestRoute(
                    schedule.get(i - 1),
                    schedule.get(i)));
        }
        return combineRoutes(partialRoutes);
    }

    private Optional<Matching> matchPendingVehicle(Request request, Vehicle vehicle) {
        if (vehicle.getResidualCapacity() < request.getLoad()) {
            return Optional.empty();
        }

        var checkpoints = new ArrayList<Integer>();
        checkpoints.add(vehicle.getRoadBinding().getRoad().getEndNode().getNodeId());
        checkpoints.add(request.getPickupNodeId());
        checkpoints.add(request.getDropoffNodeId());
        var bestRoute = getRouteToCompleteSchedule(checkpoints);

        var schedule = new ArrayList<ScheduleNode>();
        schedule.add(createPickupScheduleNode(request));
        schedule.add(createDropoffScheduleNode(request));

        return Optional.of(new Matching(
                bestRoute.getRoute(),
                bestRoute.getCost(),
                bestRoute.getCost() + (1 - vehicle.getRoadBinding().getProgress()) * vehicle.getRoadBinding().getRoad().getCost(),
                schedule,
                vehicle
        ));
    }

    private boolean checkCapacityViolation(Vehicle vehicle, List<ScheduleNode> schedule) {
        int capacity = vehicle.getResidualCapacity();
        for (var node : schedule) {
            if (node.getKind() == ScheduleNodeKind.PICKUP) {
                if (capacity < node.getLoad()) {
                    return false;
                }
                capacity -= node.getLoad();
            } else if (node.getKind() == ScheduleNodeKind.DROPOFF) {
                capacity += node.getLoad();
            } else {
                log.warn("unknown schedule node kind " + node.getKind());
                return false;
            }
        }
        return true;
    }

    /**
     * Проверяет нарушение дедлайнов прибытия в контрольные точки плана.
     * Если план не нарушает дедлайны то, возвращается наилучший маршрут для выполнения плана.
     * Если план нарушает дедлайны, то возвращается пустой Optional
     */
    private Optional<RoadRoute> checkDeadlineViolation(Vehicle vehicle, List<ScheduleNode> schedule) {
        var partialRoutes = new ArrayList<RoadRoute>();
        long time = Instant.now()
                .plusSeconds((long) ((1 - vehicle.getRoadBinding().getProgress()) * vehicle.getRoadBinding().getRoad().getCost()))
                .getEpochSecond();

        int lastNode = vehicle.getRoadBinding().getRoad().getEndNode().getNodeId();
        for (var node : schedule) {
            var route = roadIndex.shortestRoute(lastNode, node.getNodeId());
            time += (long) (route.getCost());
            if (time > node.getDeadline().getEpochSecond()) {
                return Optional.empty();
            }
            partialRoutes.add(route);
            lastNode = node.getNodeId();
        }

        return Optional.of(combineRoutes(partialRoutes));
    }

    private Optional<Matching> matchServingVehicle(Request request, Vehicle vehicle) {
        if (vehicle.getResidualCapacity() < request.getLoad()) {
            return Optional.empty();
        }

        var pickupNode = createPickupScheduleNode(request);
        var dropoffNode = createDropoffScheduleNode(request);

        var augmentedSchedules = new LazyScheduleGenerator(vehicle.getSchedule(), pickupNode, dropoffNode).getAllSchedules();

        Matching bestMatching = new Matching(
                Collections.emptyList(),
                Double.POSITIVE_INFINITY,
                Double.POSITIVE_INFINITY,
                Collections.emptyList(),
                null);

        for (var schedule : augmentedSchedules) {
            if (!checkCapacityViolation(vehicle, schedule)) {
                continue;
            }
            var route = checkDeadlineViolation(vehicle, schedule);
            if (route.isEmpty()) {
                continue;
            }
            if (route.get().getCost() - vehicle.getDistanceScheduled() < bestMatching.additionalCost) {
                bestMatching = new Matching(
                        route.get().getRoute(),
                        route.get().getCost(),
                        route.get().getCost() - vehicle.getDistanceScheduled(),
                        schedule,
                        vehicle
                );
            }
        }

        if (bestMatching.bestSchedule.isEmpty()) {
            return Optional.empty();
        }

        return Optional.of(bestMatching);
    }

    @Override public List<Vehicle> getTopKCandidateVehicles(Request request, int kCandidates) {
        // Валидации

        var candidateVehicles = filterVehicles(request);
        var topKCandidates = MinMaxPriorityQueue
                .orderedBy(Comparator.<Matching>naturalOrder())
                .maximumSize(kCandidates)
                .create();

        for (var candidate : candidateVehicles) {
            if (candidate.getStatus() == VehicleStatus.PENDING) {
                var matching = matchPendingVehicle(request, candidate);
                if (matching.isEmpty()) {
                    continue;
                }
                topKCandidates.add(matching.get());
            } else if (candidate.getStatus() == VehicleStatus.SERVING) {
                var matching = matchServingVehicle(request, candidate);
                if (matching.isEmpty()) {
                    continue;
                }
                topKCandidates.add(matching.get());
            } else {
                log.warn("unknown vehicle status " + candidate.getStatus());
            }
        }

        return topKCandidates.stream()
                .sorted(Comparator.naturalOrder())
                .map(Matching::getVehicle)
                .collect(Collectors.toUnmodifiableList());
    }

    @Override public void acceptRequest(Vehicle vehicle, Request request) {

    }

    @Override public void cancelRequest(Vehicle vehicle, Request request) {

    }

    private static class Matching implements Comparable<Matching> {
        private List<RoadNode> bestRoute;
        private double bestRouteCost;
        private double additionalCost;
        private List<ScheduleNode> bestSchedule;
        private Vehicle vehicle;

        public Matching(List<RoadNode> bestRoute, double bestRouteCost, double additionalCost, List<ScheduleNode> bestSchedule, Vehicle vehicle) {
            this.bestRoute = bestRoute;
            this.bestRouteCost = bestRouteCost;
            this.additionalCost = additionalCost;
            this.bestSchedule = bestSchedule;
            this.vehicle = vehicle;
        }

        public double getAdditionalCost() {
            return additionalCost;
        }

        public List<RoadNode> getBestRoute() {
            return bestRoute;
        }

        public double getBestRouteCost() {
            return bestRouteCost;
        }

        public List<ScheduleNode> getBestSchedule() {
            return bestSchedule;
        }

        public Vehicle getVehicle() {
            return vehicle;
        }

        @Override public int compareTo(Matching o) {
            if (o == null) {
                throw new NullPointerException();
            }
            return Double.compare(this.additionalCost, o.additionalCost);
        }

        @Override public String toString() {
            return "Matching{" +
                    "bestRoute=" + bestRoute +
                    ", bestRoadCost=" + bestRouteCost +
                    ", bestSchedule=" + bestSchedule +
                    ", vehicle=" + vehicle +
                    '}';
        }
    }
}
