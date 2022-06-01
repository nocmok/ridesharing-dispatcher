package com.nocmok.orp.solver.ls;

import com.google.common.collect.MinMaxPriorityQueue;
import com.nocmok.orp.graph.api.Node;
import com.nocmok.orp.graph.api.Route;
import com.nocmok.orp.graph.api.Segment;
import com.nocmok.orp.graph.api.ShortestRouteSolver;
import com.nocmok.orp.graph.api.SpatialGraphMetadataStorage;
import com.nocmok.orp.graph.api.SpatialGraphObject;
import com.nocmok.orp.graph.api.SpatialGraphObjectsStorage;
import com.nocmok.orp.solver.api.OrpSolver;
import com.nocmok.orp.solver.api.Request;
import com.nocmok.orp.solver.api.RequestCancellation;
import com.nocmok.orp.solver.api.RequestMatching;
import com.nocmok.orp.solver.api.RouteNode;
import com.nocmok.orp.solver.api.Schedule;
import com.nocmok.orp.solver.api.ScheduleEntry;
import com.nocmok.orp.solver.api.ScheduleEntryKind;
import com.nocmok.orp.state_keeper.api.StateKeeper;
import com.nocmok.orp.state_keeper.api.VehicleState;
import com.nocmok.orp.state_keeper.api.VehicleStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class LSSolver implements OrpSolver {

    private static final Logger log = LoggerFactory.getLogger(LSSolver.class);

    private final SpatialGraphMetadataStorage graphMetadataStorage;
    private final SpatialGraphObjectsStorage graphObjectsStorage;
    private final ShortestRouteSolver shortestRouteSolver;
    private final StateKeeper<? extends VehicleState> stateKeeper;
    private final RoadProgressStrategy roadProgressStrategy;

    public LSSolver(SpatialGraphMetadataStorage graphMetadataStorage,
                    SpatialGraphObjectsStorage graphObjectsStorage,
                    ShortestRouteSolver shortestRouteSolver,
                    StateKeeper<? extends VehicleState> stateKeeper) {
        this.graphMetadataStorage = graphMetadataStorage;
        this.graphObjectsStorage = graphObjectsStorage;
        this.shortestRouteSolver = shortestRouteSolver;
        this.stateKeeper = stateKeeper;
        this.roadProgressStrategy = new DumbRoadProgressStrategy();
    }

    private Double getVehicleProgressOnCurrentRoad(SpatialGraphObject vehicle) {
        return roadProgressStrategy.getProgress(
                vehicle.getSegment().getStartNode().getLatitude(),
                vehicle.getSegment().getStartNode().getLongitude(),
                vehicle.getSegment().getEndNode().getLatitude(),
                vehicle.getSegment().getEndNode().getLongitude(),
                vehicle.getLatitude(),
                vehicle.getLongitude()
        );
    }

    private Optional<ExtendedVehicle> enrichVehicleWithGeoData(VehicleState vehicle) {
        return graphObjectsStorage.getObject(vehicle.getId())
                .map(graphObject -> new ExtendedVehicle(
                        vehicle,
                        graphObject.getLatitude(),
                        graphObject.getLongitude(),
                        graphObject.getSegment(),
                        getVehicleProgressOnCurrentRoad(graphObject),
                        getRouteToCompleteSchedule(Stream.concat(
                                Stream.of(graphObject.getSegment().getStartNode().getId()),
                                vehicle.getSchedule().asList().stream().map(ScheduleEntry::getNodeId)
                        ).collect(Collectors.toList()))
                ));
    }

    private List<ExtendedVehicle> enrichVehiclesWithGeoData(List<? extends VehicleState> vehicles) {
        // TODO Обогащать батчем
        return vehicles.stream()
                .map(this::enrichVehicleWithGeoData)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toCollection(ArrayList::new));
    }

    private ExtendedRequest enrichRequestWithGeoData(Request request) {
        var pickupRoadSegment = graphMetadataStorage.getSegment(request.getPickupRoadSegment().getStartNodeId(), request.getPickupRoadSegment().getEndNodeId());
        var dropOffRoadSegment =
                graphMetadataStorage.getSegment(request.getDropOffRoadSegment().getStartNodeId(), request.getDropOffRoadSegment().getEndNodeId());
        return new ExtendedRequest(
                request, pickupRoadSegment, dropOffRoadSegment,
                pickupRoadSegment.getCost() * roadProgressStrategy.getProgress(
                        pickupRoadSegment.getStartNode().getLatitude(),
                        pickupRoadSegment.getStartNode().getLongitude(),
                        pickupRoadSegment.getEndNode().getLatitude(),
                        pickupRoadSegment.getEndNode().getLongitude(),
                        request.getRecordedOriginLatitude(),
                        request.getRecordedOriginLongitude()
                ),
                dropOffRoadSegment.getCost() * roadProgressStrategy.getProgress(
                        dropOffRoadSegment.getStartNode().getLatitude(),
                        dropOffRoadSegment.getStartNode().getLongitude(),
                        dropOffRoadSegment.getEndNode().getLatitude(),
                        dropOffRoadSegment.getEndNode().getLongitude(),
                        request.getRecordedDestinationLatitude(),
                        request.getRecordedDestinationLongitude()
                )
        );
    }

    /**
     * Отсекает тс, которые точно не могут обработать запрос
     */
    private List<ExtendedVehicle> filterVehicles(ExtendedRequest request) {
        // TODO добавить двустороннюю фильтрацию
        long timeReserveSeconds = request.getRequestedAt().getEpochSecond() + request.getMaxPickupDelaySeconds() - Instant.now().getEpochSecond();
        long timeOnPickupRoadSegment = request.getTimeOnPickupRoadSegment().longValue();

        List<String> filteredVehiclesId = graphObjectsStorage
                .getNeighborhood(request.getPickupRoadSegment().getStartNode().getId(), timeReserveSeconds - timeOnPickupRoadSegment)
                .stream()
                .map(SpatialGraphObject::getId)
                .collect(Collectors.toList());

        return enrichVehiclesWithGeoData(stateKeeper.getVehiclesByIds(filteredVehiclesId));
    }

    private ScheduleEntry createPickupScheduleEntry(ExtendedRequest request) {
        return new ScheduleEntry(
                request.getRequestedAt().plusSeconds(request.getMaxPickupDelaySeconds()),
                request.getLoad(),
                request.getOriginNodeId(),
                request.getRecordedOriginLatitude(),
                request.getRecordedOriginLongitude(),
                ScheduleEntryKind.PICKUP,
                request.getRequestId()
        );
    }

    private ScheduleEntry createDropoffScheduleEntry(ExtendedRequest request) {
        Instant deadline = request.getRequestedAt()
                .plusSeconds(request.getMaxPickupDelaySeconds())
                .plusSeconds((long) (shortestRouteSolver.getShortestRoute(
                        request.getOriginNodeId(),
                        request.getDestinationNodeId()).getRouteCost() * request.getDetourConstraint()));
        return new ScheduleEntry(
                deadline,
                request.getLoad(),
                request.getDestinationNodeId(),
                request.getRecordedDestinationLatitude(),
                request.getRecordedDestinationLongitude(),
                ScheduleEntryKind.DROPOFF,
                request.getRequestId()
        );
    }

    /**
     * Объединяет цепочку смежных по вершине маршрутов в один маршрут
     */
    private NodesRoute combineRoutes(List<NodesRoute> routes) {
        var combinedRoute = new ArrayList<Node>();
        double combinedCost = 0;

        for (var route : routes) {
            if (!combinedRoute.isEmpty() && !route.getRoute().isEmpty()) {
                combinedRoute.remove(combinedRoute.size() - 1);
            }
            combinedRoute.addAll(route.getRoute());
            combinedCost += route.getRouteCost();
        }

        return new NodesRoute(combinedRoute, combinedCost);
    }

    private NodesRoute mapSegmentsRouteToNodesRoute(Route segmentsRoute) {
        if (segmentsRoute.isEmpty()) {
            return NodesRoute.emptyRoute();
        }
        var nodes = segmentsRoute.getRoute().stream()
                .map(Segment::getStartNode)
                .collect(Collectors.toCollection(ArrayList::new));
        nodes.add(segmentsRoute.getRoute().get(segmentsRoute.size() - 1).getEndNode());
        return new NodesRoute(nodes, segmentsRoute.getRouteCost());
    }

    /**
     * Принимает список идентификаторов вершин привязанных к контрольным точкам.
     * Возвращает маршрут, который обходит переданные точки в заданном порядке
     */
    private NodesRoute getRouteToCompleteSchedule(List<String> schedule) {
        var partialRoutes = new ArrayList<NodesRoute>();
        for (int i = 1; i < schedule.size(); ++i) {
            partialRoutes.add(mapSegmentsRouteToNodesRoute(shortestRouteSolver.getShortestRoute(
                    schedule.get(i - 1),
                    schedule.get(i))));
        }
        return combineRoutes(partialRoutes);
    }

    private RouteNode mapNodeToRouteNode(Node node) {
        return new RouteNode(node.getId(), node.getLatitude(), node.getLongitude());
    }

    private Optional<RequestMatching> matchPendingVehicle(ExtendedRequest request, ExtendedVehicle vehicle) {
        if (vehicle.getResidualCapacity() < request.getLoad()) {
            return Optional.empty();
        }

        var checkpoints = new ArrayList<String>();
        checkpoints.add(vehicle.getRoadSegment().getEndNode().getId());
        checkpoints.add(request.getOriginNodeId());
        checkpoints.add(request.getDestinationNodeId());
        var bestRoute = getRouteToCompleteSchedule(checkpoints);

        var schedule = new ArrayList<ScheduleEntry>();
        schedule.add(createPickupScheduleEntry(request));
        schedule.add(createDropoffScheduleEntry(request));

        return Optional.of(new RequestMatching(
                vehicle.getId(),

                vehicle.getSchedule(),
                new ListSchedule(schedule),

                bestRoute.getRoute().stream()
                        .map(this::mapNodeToRouteNode)
                        .collect(Collectors.toList()),
                bestRoute.getRouteCost() + vehicle.getCostToNextNodeInScheduledRoute()
        ));
    }

    private boolean checkCapacityViolation(ExtendedVehicle vehicle, List<ScheduleEntry> schedule) {
        int capacity = vehicle.getResidualCapacity();
        for (var node : schedule) {
            if (node.getKind() == ScheduleEntryKind.PICKUP) {
                if (capacity < node.getLoad()) {
                    return false;
                }
                capacity -= node.getLoad();
            } else if (node.getKind() == ScheduleEntryKind.DROPOFF) {
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
    private Optional<NodesRoute> checkDeadlineViolationAndGetRoute(ExtendedVehicle vehicle, List<ScheduleEntry> schedule) {
        var partialRoutes = new ArrayList<NodesRoute>();

        long time = Instant.now()
                .plusSeconds(vehicle.getCostToNextNodeInScheduledRoute().longValue())
                .getEpochSecond();

        var lastNode = vehicle.getRoadSegment().getEndNode().getId();

        for (var node : schedule) {
            var route = shortestRouteSolver.getShortestRoute(lastNode, node.getNodeId());

            time += route.getRouteCost().longValue();

            if (time > node.getDeadline().getEpochSecond()) {
                return Optional.empty();
            }

            partialRoutes.add(mapSegmentsRouteToNodesRoute(route));

            lastNode = node.getNodeId();
        }

        return Optional.of(combineRoutes(partialRoutes));
    }

    private Optional<RequestMatching> matchServingVehicle(ExtendedRequest request, ExtendedVehicle vehicle) {
        if (vehicle.getResidualCapacity() < request.getLoad()) {
            return Optional.empty();
        }

        // Стоимость текущего маршрута тс без учета дорожного сегмента, на котором находится тс в текущий момент времени
        double scheduledRouteCost = vehicle.getRouteScheduled().getRouteCost() - vehicle.getRoadSegment().getCost();

        var pickupNode = createPickupScheduleEntry(request);
        var dropoffNode = createDropoffScheduleEntry(request);

        var augmentedSchedules = new LazyScheduleGenerator(vehicle.getSchedule(), pickupNode, dropoffNode).getAllSchedules();

        double bestAdditionalCost = Double.POSITIVE_INFINITY;
        List<ScheduleEntry> bestSchedule = Collections.emptyList();
        List<Node> bestRoute = Collections.emptyList();

        for (var schedule : augmentedSchedules) {
            if (!checkCapacityViolation(vehicle, schedule)) {
                continue;
            }
            var route = checkDeadlineViolationAndGetRoute(vehicle, schedule);
            if (route.isEmpty()) {
                continue;
            }
            if (route.get().getRouteCost() - scheduledRouteCost < bestAdditionalCost) {
                bestAdditionalCost = route.get().getRouteCost() - scheduledRouteCost;
                bestSchedule = schedule;
                bestRoute = route.get().getRoute();
            }
        }

        if (bestSchedule.isEmpty()) {
            return Optional.empty();
        }

        return Optional.of(new RequestMatching(
                vehicle.getId(),

                vehicle.getSchedule(),
                new ListSchedule(bestSchedule),

                bestRoute.stream()
                        .map(this::mapNodeToRouteNode)
                        .collect(Collectors.toList()),
                bestAdditionalCost
        ));
    }

    @Override public List<RequestMatching> getTopKCandidateVehicles(Request request, int kCandidates) {
        var extendedRequest = enrichRequestWithGeoData(request);
        return getTopKCandidateVehiclesInternal(extendedRequest, kCandidates);
    }

    public List<RequestMatching> getTopKCandidateVehiclesInternal(ExtendedRequest request, int kCandidates) {
        // Валидации

        var candidateVehicles = filterVehicles(request);
        var topKCandidates = MinMaxPriorityQueue
                .orderedBy(Comparator.comparingDouble(RequestMatching::getAdditionalCost))
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
                .sorted(Comparator.comparingDouble(RequestMatching::getAdditionalCost))
                .collect(Collectors.toUnmodifiableList());
    }

    private Optional<RequestMatching> getRequestMatchingForVehicleInternal(ExtendedRequest request, ExtendedVehicle vehicle) {
        if (vehicle.getStatus() == VehicleStatus.PENDING) {
            return matchPendingVehicle(request, vehicle);
        } else if (vehicle.getStatus() == VehicleStatus.SERVING) {
            return matchServingVehicle(request, vehicle);
        } else {
            throw new RuntimeException("unknown vehicle status " + vehicle.getStatus());
        }
    }

    @Override public Optional<RequestMatching> getRequestMatchingForVehicle(Request request, String vehicleId) {
        var vehicle = stateKeeper.getVehiclesByIds(List.of(vehicleId)).stream().findFirst();
        if (vehicle.isEmpty()) {
            throw new IllegalArgumentException("vehicle with id " + vehicleId + ", does not exist");
        }
        var extendedVehicle = enrichVehicleWithGeoData(vehicle.get());
        if (extendedVehicle.isEmpty()) {
            throw new IllegalArgumentException("vehicle with id " + vehicleId + ", does not exist in graph index");
        }
        return getRequestMatchingForVehicleInternal(enrichRequestWithGeoData(request), extendedVehicle.get());
    }

    private Optional<RequestCancellation> cancelRequestInternal(Request request, ExtendedVehicle vehicle) {
        var newSchedule = vehicle.getSchedule().asList().stream()
                .filter(scheduleEntry -> !Objects.equals(scheduleEntry.getOrderId(), request.getRequestId()))
                .collect(Collectors.toList());

        var newRouteCheckpoints = Stream.concat(
                Stream.of(vehicle.getRoadSegment().getStartNode().getId()),
                newSchedule.stream().map(ScheduleEntry::getNodeId)).collect(Collectors.toList());

        var newRoute = getRouteToCompleteSchedule(newRouteCheckpoints).getRoute().stream()
                .map(this::mapNodeToRouteNode)
                .collect(Collectors.toList());

        return Optional.of(new RequestCancellation(new ListSchedule(newSchedule), newRoute));
    }

    private boolean scheduleContainsRequest(Schedule schedule, Request request) {
        return schedule.asList().stream()
                .map(ScheduleEntry::getOrderId)
                .anyMatch(request.getRequestId()::equals);
    }


    @Override public Optional<RequestCancellation> cancelRequest(Request request, String vehicleId) {
        var vehicle = stateKeeper.getActiveVehiclesByIdsForUpdate(List.of(vehicleId)).stream().findFirst()
                .orElseThrow(() -> new IllegalArgumentException("vehicle with id " + vehicleId + " does not exist"));

        if (!scheduleContainsRequest(vehicle.getSchedule(), request)) {
            return Optional.empty();
        }

        var extendedVehicle = enrichVehicleWithGeoData(vehicle).orElseThrow(
                () -> new IllegalArgumentException("vehicle with id " + vehicleId + ", does not present in graph index"));

        return cancelRequestInternal(request, extendedVehicle);
    }
}
