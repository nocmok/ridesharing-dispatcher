package com.nocmok.orp.solver.ls;

import com.google.common.collect.MinMaxPriorityQueue;
import com.nocmok.orp.graph.api.SpatialGraphMetadataStorage;
import com.nocmok.orp.graph.api.SpatialGraphObject;
import com.nocmok.orp.graph.api.SpatialGraphObjectsStorage;
import com.nocmok.orp.graph.api.Node;
import com.nocmok.orp.graph.api.Route;
import com.nocmok.orp.graph.api.Segment;
import com.nocmok.orp.graph.api.ShortestRouteSolver;
import com.nocmok.orp.solver.api.OrpSolver;
import com.nocmok.orp.solver.api.Request;
import com.nocmok.orp.solver.api.RequestMatching;
import com.nocmok.orp.solver.api.RouteNode;
import com.nocmok.orp.solver.api.ScheduleNode;
import com.nocmok.orp.solver.api.ScheduleNodeKind;
import com.nocmok.orp.state_keeper.api.ScheduleEntry;
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
                                vehicle.getSchedule().stream().map(ScheduleEntry::getNodeId)
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

    private ScheduleNode createPickupScheduleNode(ExtendedRequest request) {
        return new ScheduleNode(
                request.getRequestedAt().plusSeconds(request.getMaxPickupDelaySeconds()),
                request.getLoad(),
                request.getOriginNodeId(),
                request.getRecordedOriginLatitude(),
                request.getRecordedOriginLongitude(),
                ScheduleNodeKind.PICKUP,
                request.getRequestId()
        );
    }

    private ScheduleNode createDropoffScheduleNode(ExtendedRequest request) {
        Instant deadline = request.getRequestedAt()
                .plusSeconds(request.getMaxPickupDelaySeconds())
                .plusSeconds((long) (shortestRouteSolver.getShortestRoute(
                        request.getOriginNodeId(),
                        request.getDestinationNodeId()).getRouteCost() * request.getDetourConstraint()));
        return new ScheduleNode(
                deadline,
                request.getLoad(),
                request.getDestinationNodeId(),
                request.getRecordedDestinationLatitude(),
                request.getRecordedDestinationLongitude(),
                ScheduleNodeKind.DROPOFF,
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
            if (!combinedRoute.isEmpty()) {
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

        var schedule = new ArrayList<ScheduleNode>();
        schedule.add(createPickupScheduleNode(request));
        schedule.add(createDropoffScheduleNode(request));

        return Optional.of(new RequestMatching(
                vehicle.getId(),
                vehicle.getSchedule(),
                schedule,
                bestRoute.getRoute().stream()
                        .map(this::mapNodeToRouteNode)
                        .collect(Collectors.toList()),
                bestRoute.getRouteCost() + vehicle.getCostToNextNodeInScheduledRoute()
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
    private Optional<NodesRoute> checkDeadlineViolationAndGetRoute(ExtendedVehicle vehicle, List<ScheduleNode> schedule) {
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

    private Double getRouteCost(List<RouteNode> route) {
        if (route.size() < 2) {
            return 0d;
        }

        var routeNodesIds = route.stream()
                .map(RouteNode::getNodeId)
                .map(Object::toString)
                .collect(Collectors.toList());

        var routeSegments = graphMetadataStorage.getSegments(
                routeNodesIds.subList(0, routeNodesIds.size() - 1),
                routeNodesIds.subList(1, routeNodesIds.size())
        );

        return routeSegments.stream()
                .map(Segment::getCost)
                .reduce(0d, Double::sum);
    }

    private Optional<RequestMatching> matchServingVehicle(ExtendedRequest request, ExtendedVehicle vehicle) {
        if (vehicle.getResidualCapacity() < request.getLoad()) {
            return Optional.empty();
        }

        // Стоимость текущего маршрута тс без учета дорожного сегмента, на котором находится тс в текущий момент времени
        double scheduledRouteCost = vehicle.getRouteScheduled().getRouteCost() - vehicle.getRoadSegment().getCost();

        var pickupNode = createPickupScheduleNode(request);
        var dropoffNode = createDropoffScheduleNode(request);

        var augmentedSchedules = new LazyScheduleGenerator(vehicle.getSchedule(), pickupNode, dropoffNode).getAllSchedules();

        double bestAdditionalCost = Double.POSITIVE_INFINITY;
        List<ScheduleNode> bestSchedule = Collections.emptyList();
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
                bestSchedule,
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
}
