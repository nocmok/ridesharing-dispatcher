package com.nocmok.orp.solver.ls;

import com.google.common.collect.MinMaxPriorityQueue;
import com.nocmok.orp.core_api.GCS;
import com.nocmok.orp.core_api.GraphIndex;
import com.nocmok.orp.core_api.GraphIndexEntity;
import com.nocmok.orp.core_api.GraphNode;
import com.nocmok.orp.core_api.GraphRoute;
import com.nocmok.orp.core_api.OrpSolver;
import com.nocmok.orp.core_api.Request;
import com.nocmok.orp.core_api.RequestMatching;
import com.nocmok.orp.core_api.ScheduleNode;
import com.nocmok.orp.core_api.ScheduleNodeKind;
import com.nocmok.orp.core_api.StateKeeper;
import com.nocmok.orp.core_api.Vehicle;
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
import java.util.stream.Stream;

public class LSSolver implements OrpSolver {

    private static Logger log = LoggerFactory.getLogger(LSSolver.class);
    private GraphIndex roadIndex;
    private StateKeeper<? extends Vehicle> vehicleStateService;

    public LSSolver(GraphIndex roadIndex, StateKeeper<? extends Vehicle> vehicleStateService) {
        this.roadIndex = roadIndex;
        this.vehicleStateService = vehicleStateService;
    }

    private List<ExtendedVehicle> enrichVehicleWithGeoData(List<? extends Vehicle> vehicles) {
        // TODO Обогащать батчем
        var extendedVehicles = new ArrayList<ExtendedVehicle>();
        for (var vehicle : vehicles) {
            var extendedVehicle = new ExtendedVehicle(vehicle);
            extendedVehicle.setCostToNextNodeInScheduledRoute(
                    roadIndex.getRouteCost(List.of(vehicle.getRoadBinding().getRoad().getStartNode(), vehicle.getRoadBinding().getRoad().getEndNode())) *
                            (1 - vehicle.getRoadBinding().getProgress()));
            extendedVehicles.add(extendedVehicle);
        }
        return extendedVehicles;
    }

    private ExtendedVehicle enrichVehicleWithGeoData(Vehicle vehicle) {
        return enrichVehicleWithGeoData(List.of(vehicle)).get(0);
    }

    /**
     * Отсекает тс, которые точно не могут обработать запрос
     */
    private List<ExtendedVehicle> filterVehicles(Request request) {
        // TODO добавить двустороннюю фильтрацию
        long timeReserveSeconds = request.getRequestedAt().getEpochSecond() + request.getMaxPickupDelaySeconds() - Instant.now().getEpochSecond();
        List<String> filteredVehiclesId = roadIndex
                .getNeighborhood(new GCS(request.getPickupLat(), request.getPickupLon()), timeReserveSeconds)
                .stream()
                .map(GraphIndexEntity::getId)
                .collect(Collectors.toList());

        return enrichVehicleWithGeoData(vehicleStateService.getVehiclesByIds(filteredVehiclesId));
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
    private GraphRoute combineRoutes(List<GraphRoute> routes) {
        var combinedRoute = new ArrayList<GraphNode>();
        double combinedCost = 0;
        for (var route : routes) {
            if (!combinedRoute.isEmpty()) {
                combinedRoute.remove(combinedRoute.size() - 1);
            }
            combinedRoute.addAll(route.getRoute());
            combinedCost += route.getCost();
        }
        return new GraphRoute(combinedRoute, combinedCost);
    }

    /**
     * Принимает список идентификаторов вершин привязанных к контрольным точкам.
     * Возвращает маршрут, который обходит переданные точки в заданном порядке
     */
    private GraphRoute getRouteToCompleteSchedule(List<Integer> schedule) {
        var partialRoutes = new ArrayList<GraphRoute>();
        for (int i = 1; i < schedule.size(); ++i) {
            partialRoutes.add(roadIndex.shortestRoute(
                    schedule.get(i - 1),
                    schedule.get(i)));
        }
        return combineRoutes(partialRoutes);
    }

    private Optional<Matching> matchPendingVehicle(Request request, ExtendedVehicle vehicle) {
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
                request,
                Stream.concat(Stream.of(vehicle.getRoadBinding().getRoad().getStartNode()), bestRoute.getRoute().stream())
                        .collect(Collectors.toList()),
                bestRoute.getCost(),
                bestRoute.getCost() + vehicle.getCostToNextNodeInScheduledRoute(),
                schedule,
                vehicle.getUnderlyingVehicle()
        ));
    }

    private boolean checkCapacityViolation(ExtendedVehicle vehicle, List<ScheduleNode> schedule) {
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
     * Если план нарушает дедлайны, то возвращается пустой Optional.
     * Возвращает маршрут без текущего ребра тс
     */
    private Optional<GraphRoute> checkDeadlineViolationAndGetRoute(ExtendedVehicle vehicle, List<ScheduleNode> schedule) {
        var partialRoutes = new ArrayList<GraphRoute>();
        long time = Instant.now()
                .plusSeconds(vehicle.getCostToNextNodeInScheduledRoute().longValue())
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

    private Optional<Matching> matchServingVehicle(Request request, ExtendedVehicle vehicle) {
        if (vehicle.getResidualCapacity() < request.getLoad()) {
            return Optional.empty();
        }

        // Запланированный маршрут тс без текущего ребра
        var scheduledRoute = new GraphRoute(
                vehicle.getRouteScheduled().subList(1, vehicle.getRouteScheduled().size()),
                roadIndex.getRouteCost(vehicle.getRouteScheduled().subList(1, vehicle.getRouteScheduled().size()))
        );

        var pickupNode = createPickupScheduleNode(request);
        var dropoffNode = createDropoffScheduleNode(request);

        var augmentedSchedules = new LazyScheduleGenerator(vehicle.getSchedule(), pickupNode, dropoffNode).getAllSchedules();

        Matching bestMatching = new Matching(
                request,
                Collections.emptyList(),
                Double.POSITIVE_INFINITY,
                Double.POSITIVE_INFINITY,
                Collections.emptyList(),
                null);

        for (var schedule : augmentedSchedules) {
            if (!checkCapacityViolation(vehicle, schedule)) {
                continue;
            }
            var route = checkDeadlineViolationAndGetRoute(vehicle, schedule);
            if (route.isEmpty()) {
                continue;
            }
            if (route.get().getCost() - scheduledRoute.getCost() < bestMatching.additionalCost) {
                bestMatching = new Matching(
                        request,
                        Stream.concat(Stream.of(vehicle.getRoadBinding().getRoad().getStartNode()), route.get().getRoute().stream())
                                .collect(Collectors.toList()),
                        route.get().getCost(),
                        route.get().getCost() - scheduledRoute.getCost(),
                        schedule,
                        vehicle.getUnderlyingVehicle()
                );
            }
        }

        if (bestMatching.bestSchedule.isEmpty()) {
            return Optional.empty();
        }

        return Optional.of(bestMatching);
    }

    private RequestMatching mapInternalMatchingToRequestMatching(Matching matching) {
        return new RequestMatching(
                matching.getRequest(),
                matching.getVehicle(),
                matching.getAdditionalCost(),
                matching.getBestRoute(),
                matching.getBestSchedule()
        );
    }

    @Override public List<RequestMatching> getTopKCandidateVehicles(Request request, int kCandidates) {
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
                .map(this::mapInternalMatchingToRequestMatching)
                .collect(Collectors.toUnmodifiableList());
    }

    @Override public void acceptRequest(Vehicle vehicle, Request request) {
        if (vehicle.getStatus() == VehicleStatus.PENDING) {
            var extendedVehicle = enrichVehicleWithGeoData(vehicle);
            var matching = matchPendingVehicle(request, extendedVehicle);
            if (matching.isEmpty()) {
                throw new RuntimeException("unable to match vehicle " + vehicle + ", against request " + request);
            }
            vehicle.setSchedule(matching.get().getBestSchedule());
            vehicle.setRouteScheduled(matching.get().getBestRoute());
        } else if (vehicle.getStatus() == VehicleStatus.SERVING) {
            var extendedVehicle = enrichVehicleWithGeoData(vehicle);
            var matching = matchServingVehicle(request, extendedVehicle);
            if (matching.isEmpty()) {
                throw new RuntimeException("unable to match vehicle " + vehicle + ", against request " + request);
            }
            vehicle.setSchedule(matching.get().getBestSchedule());
            vehicle.setRouteScheduled(matching.get().getBestRoute());
        } else {
            throw new RuntimeException("unknown vehicle status " + vehicle.getStatus());
        }
    }

    @Override public void cancelRequest(Vehicle vehicle, Request request) {

    }

    private static class Matching implements Comparable<Matching> {
        private Request request;
        private List<GraphNode> bestRoute;
        private double bestRouteCost;
        private double additionalCost;
        private List<ScheduleNode> bestSchedule;
        private Vehicle vehicle;

        public Matching(Request request, List<GraphNode> bestRoute, double bestRouteCost, double additionalCost,
                        List<ScheduleNode> bestSchedule, Vehicle vehicle) {
            this.request = request;
            this.bestRoute = bestRoute;
            this.bestRouteCost = bestRouteCost;
            this.additionalCost = additionalCost;
            this.bestSchedule = bestSchedule;
            this.vehicle = vehicle;
        }

        public Request getRequest() {
            return request;
        }

        public double getAdditionalCost() {
            return additionalCost;
        }

        /**
         * Возвращает лучший маршрут включая текущее ребро тс
         */
        public List<GraphNode> getBestRoute() {
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
                    "request=" + request +
                    ", bestRoute=" + bestRoute +
                    ", bestRouteCost=" + bestRouteCost +
                    ", additionalCost=" + additionalCost +
                    ", bestSchedule=" + bestSchedule +
                    ", vehicle=" + vehicle +
                    '}';
        }
    }
}
