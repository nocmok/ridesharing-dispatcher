package com.nocmok.orp.solver.kt;

import com.google.common.collect.MinMaxPriorityQueue;
import com.nocmok.orp.graph.api.Node;
import com.nocmok.orp.graph.api.Route;
import com.nocmok.orp.graph.api.Segment;
import com.nocmok.orp.graph.api.ShortestRouteSolver;
import com.nocmok.orp.graph.api.SpatialGraphMetadataStorage;
import com.nocmok.orp.graph.api.SpatialGraphObject;
import com.nocmok.orp.graph.api.SpatialGraphObjectsStorage;
import com.nocmok.orp.solver.api.EmptySchedule;
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
import lombok.extern.slf4j.Slf4j;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
public class KTSolver implements OrpSolver {

    private final SpatialGraphMetadataStorage graphMetadataStorage;
    private final SpatialGraphObjectsStorage graphObjectsStorage;
    private final ShortestRouteSolver shortestRouteSolver;
    private final StateKeeper<? extends VehicleState> stateKeeper;
    private final RoadProgressStrategy roadProgressStrategy;
    private final Integer kineticTreeSizeThreshold;
    private final Integer candidatesFetchThreshold;

    public KTSolver(SpatialGraphMetadataStorage graphMetadataStorage, SpatialGraphObjectsStorage graphObjectsStorage,
                    ShortestRouteSolver shortestRouteSolver,
                    StateKeeper<? extends VehicleState> stateKeeper, Integer kineticTreeSizeThreshold,
                    Integer candidatesFetchThreshold) {
        this.graphMetadataStorage = graphMetadataStorage;
        this.graphObjectsStorage = graphObjectsStorage;
        this.shortestRouteSolver = shortestRouteSolver;
        this.stateKeeper = stateKeeper;
        this.roadProgressStrategy = new DumbRoadProgressStrategy();
        this.kineticTreeSizeThreshold = kineticTreeSizeThreshold;
        this.candidatesFetchThreshold = candidatesFetchThreshold;
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
        // TODO ?????????????????? ????????????
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
     * ???????????????? ????, ?????????????? ?????????? ???? ?????????? ???????????????????? ????????????
     */
    private List<ExtendedVehicle> filterVehicles(ExtendedRequest request) {
        // TODO ???????????????? ???????????????????????? ????????????????????
        long timeReserveSeconds = request.getRequestedAt().getEpochSecond() + request.getMaxPickupDelaySeconds() - Instant.now().getEpochSecond();
        long timeOnPickupRoadSegment = request.getTimeOnPickupRoadSegment().longValue();

        List<String> filteredVehiclesId = graphObjectsStorage
                .getNeighborhood(request.getPickupRoadSegment().getStartNode().getId(), timeReserveSeconds - timeOnPickupRoadSegment)
                .stream()
                .limit(candidatesFetchThreshold)
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
     * ???????????????????? ?????????????? ?????????????? ???? ?????????????? ?????????????????? ?? ???????? ??????????????
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
     * ?????????????????? ???????????? ?????????????????????????????? ???????????? ?????????????????????? ?? ?????????????????????? ????????????.
     * ???????????????????? ??????????????, ?????????????? ?????????????? ???????????????????? ?????????? ?? ???????????????? ??????????????
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

        if (!vehicle.getSchedule().empty()) {
            log.warn("pending vehicle has not empty schedule");
        }

        var checkpoints = new ArrayList<String>();
        checkpoints.add(vehicle.getRoadSegment().getStartNode().getId());
        checkpoints.add(request.getOriginNodeId());
        checkpoints.add(request.getDestinationNodeId());
        var bestRoute = getRouteToCompleteSchedule(checkpoints);

        var schedule = new ArrayList<ScheduleEntry>();
        schedule.add(createPickupScheduleEntry(request));
        schedule.add(createDropoffScheduleEntry(request));

        return Optional.of(new RequestMatching(
                vehicle.getId(),
                vehicle.getSchedule(),
                new TreeSchedule(schedule),
                bestRoute.getRoute().stream()
                        .map(this::mapNodeToRouteNode)
                        .collect(Collectors.toList()),
                bestRoute.getRouteCost() + vehicle.getCostToNextNodeInScheduledRoute()
        ));
    }

    private void handleVehicleScheduleIsNotKineticTree(ExtendedRequest request, ExtendedVehicle vehicle) {
        throw new UnsupportedOperationException("cannot maintain vehicle with schedule type " + vehicle.getSchedule().getClass());
    }

    private Optional<RequestMatching> handleVehicleWithLargeKineticTree(ExtendedRequest request, ExtendedVehicle vehicle) {
        log.info(
                "vehicle with id " + vehicle.getId() + " has too large kinetic tree to process (> " + kineticTreeSizeThreshold + "). Will ignore this vehicle");
        return Optional.empty();
    }

    private Optional<RequestMatching> matchServingVehicle(ExtendedRequest request, ExtendedVehicle vehicle) {
        if (vehicle.getResidualCapacity() < request.getLoad()) {
            return Optional.empty();
        }

        if (!(vehicle.getSchedule() instanceof TreeSchedule)) {
            handleVehicleScheduleIsNotKineticTree(request, vehicle);
        }

        // ?????????????????? ???????????????? ???????????????? ???? ?????? ?????????? ?????????????????? ????????????????, ???? ?????????????? ?????????????????? ???? ?? ?????????????? ???????????? ??????????????
        double currentScheduleCost = vehicle.getRouteScheduled().getRouteCost() - vehicle.getRoadSegment().getCost();

        var pickupNode = createPickupScheduleEntry(request);
        var dropoffNode = createDropoffScheduleEntry(request);

        var kineticTree =
                new KineticTree<ScheduleEntry, ScheduleKTNode>(ScheduleKTNode::new,
                        new KineticTree.Validator<ScheduleEntry, ScheduleKTNode>() {
                            @Override public boolean validate(ScheduleKTNode parent, ScheduleKTNode child) {
                                if (child.value().getKind() == ScheduleEntryKind.PICKUP && child.value().getLoad() > child.residualCapacityBeforeEntry()) {
                                    return false;
                                }
                                return child.bestEntryTime().toEpochMilli() <= child.value().getDeadline().toEpochMilli();
                            }

                            @Override public boolean validate(ScheduleKTNode tree) {
                                if (tree.value().getKind() == ScheduleEntryKind.PICKUP && tree.value().getLoad() > tree.residualCapacityBeforeEntry()) {
                                    return false;
                                }
                                return tree.bestEntryTime().toEpochMilli() <= tree.value().getDeadline().toEpochMilli();
                            }
                        }, new KineticTree.Aggregator<ScheduleEntry, ScheduleKTNode>() {

                    @Override public void aggregate(ScheduleKTNode parent, ScheduleKTNode child) {
                        if (parent.value().getKind() == ScheduleEntryKind.PICKUP) {
                            child.residualCapacityBeforeEntry(parent.residualCapacityBeforeEntry() - parent.value().getLoad());
                        } else if (parent.value.getKind() == ScheduleEntryKind.DROPOFF) {
                            child.residualCapacityBeforeEntry(parent.residualCapacityBeforeEntry() + parent.value().getLoad());
                        } else {
                            throw new UnsupportedOperationException("unknown schedule checkpoint kind " + child.value().getKind());
                        }
                        double timeBetweenCheckpoints =
                                shortestRouteSolver.getShortestRoute(parent.value().getNodeId(), child.value().getNodeId()).getRouteCost();
                        child.bestEntryTime(parent.bestEntryTime().plusSeconds((long) timeBetweenCheckpoints));
                    }

                    @Override public void aggregate(ScheduleKTNode tree) {
                        tree.residualCapacityBeforeEntry(vehicle.getResidualCapacity());
                        // ?????????? ???? ?????????????? ?????????????? ???? + ?????????? ???? ?????????????????????? ??????????
                        double timeOnCurrentRoad = (1 - vehicle.getProgressOnRoadSegment()) * vehicle.getRoadSegment().getCost();
                        double timeToCheckpoint =
                                shortestRouteSolver.getShortestRoute(vehicle.getRoadSegment().getEndNode().getId(), tree.value().getNodeId()).getRouteCost();
                        tree.bestEntryTime(Instant.now().plusSeconds((long) (timeOnCurrentRoad + timeToCheckpoint)));
                    }
                }, ((TreeSchedule) vehicle.getSchedule()).asTree());

        // ?????????????????? ?????? ?????????? ?????????????? ?? ????????, ???? ???????? ???? ???????????? ?????????????????? ????????????,
        // ?????????? ???? ???????????? ?? ?????????????????????????? shortestRouteSolver, ???????? ???????????? ???????????????????? ?????????????? ??????????????
        kineticTree.insertWithoutHarvest(pickupNode, dropoffNode);

        // ?????????????????? ???????????? ?????????????????????????? ????????????
        if (kineticTree.size() > kineticTreeSizeThreshold) {
            return handleVehicleWithLargeKineticTree(request, vehicle);
        }

        // ???????? ???????????? ???????????? ?? ???????????? ?????????????????? ?????????????????? ???????????? ???? ???????????????????? ????????????
        kineticTree.harvest();

        var bestAugmentedScheduleOptional = kineticTree.minPermutation((a, b) -> {
            if (a.size() != b.size()) {
                throw new IllegalArgumentException("permutation sizes mismatched");
            }
            if (a.isEmpty()) {
                return 0;
            }
            return Long.compare(a.get(a.size() - 1).bestEntryTime().toEpochMilli(), b.get(b.size() - 1).bestEntryTime().toEpochMilli());
        });

        if (bestAugmentedScheduleOptional.isEmpty()) {
            return Optional.empty();
        }

        var bestAugmentedSchedule = bestAugmentedScheduleOptional.get();

        if (bestAugmentedSchedule.isEmpty()) {
            return Optional.empty();
        }

        var routeToCompleteBestAugmentedSchedule =
                getRouteToCompleteSchedule(Stream.concat(Stream.of(vehicle.getRoadSegment().getStartNode().getId()), bestAugmentedSchedule.stream()
                        .map(ScheduleEntry::getNodeId)).collect(Collectors.toList()));

        var schedulesTree = kineticTree.allPermutations();

        return Optional.of(new RequestMatching(
                vehicle.getId(),
                vehicle.getSchedule(),
                new TreeSchedule(bestAugmentedSchedule, schedulesTree),
                routeToCompleteBestAugmentedSchedule.getRoute().stream()
                        .map(this::mapNodeToRouteNode)
                        .collect(Collectors.toList()),
                routeToCompleteBestAugmentedSchedule.getRouteCost() - currentScheduleCost
        ));
    }

    @Override public List<RequestMatching> getTopKCandidateVehicles(Request request, int kCandidates) {
        var extendedRequest = enrichRequestWithGeoData(request);
        return getTopKCandidateVehiclesInternal(extendedRequest, kCandidates);
    }

    public List<RequestMatching> getTopKCandidateVehiclesInternal(ExtendedRequest request, int kCandidates) {
        // ??????????????????

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

    private Schedule listScheduleAsInternalSchedule(List<ScheduleEntry> listSchedule, ExtendedVehicle vehicle) {
        if (listSchedule == null || listSchedule.isEmpty()) {
            return new EmptySchedule();
        }
        var kineticTree = new KineticTree<ScheduleEntry, ScheduleKTNode>(ScheduleKTNode::new,
                new KineticTree.Validator<ScheduleEntry, ScheduleKTNode>() {
                    @Override public boolean validate(ScheduleKTNode parent, ScheduleKTNode child) {
                        if (child.value().getKind() == ScheduleEntryKind.PICKUP && child.value().getLoad() > child.residualCapacityBeforeEntry()) {
                            return false;
                        }
                        return child.bestEntryTime().toEpochMilli() <= child.value().getDeadline().toEpochMilli();
                    }

                    @Override public boolean validate(ScheduleKTNode tree) {
                        if (tree.value().getKind() == ScheduleEntryKind.PICKUP && tree.value().getLoad() > tree.residualCapacityBeforeEntry()) {
                            return false;
                        }
                        return tree.bestEntryTime().toEpochMilli() <= tree.value().getDeadline().toEpochMilli();
                    }
                }, new KineticTree.Aggregator<ScheduleEntry, ScheduleKTNode>() {

            @Override public void aggregate(ScheduleKTNode parent, ScheduleKTNode child) {
                if (parent.value().getKind() == ScheduleEntryKind.PICKUP) {
                    child.residualCapacityBeforeEntry(parent.residualCapacityBeforeEntry() - parent.value().getLoad());
                } else if (parent.value.getKind() == ScheduleEntryKind.DROPOFF) {
                    child.residualCapacityBeforeEntry(parent.residualCapacityBeforeEntry() + parent.value().getLoad());
                } else {
                    throw new UnsupportedOperationException("unknown schedule checkpoint kind " + child.value().getKind());
                }
                double timeBetweenCheckpoints =
                        shortestRouteSolver.getShortestRoute(parent.value().getNodeId(), child.value().getNodeId()).getRouteCost();
                child.bestEntryTime(parent.bestEntryTime().plusSeconds((long) timeBetweenCheckpoints));
            }

            @Override public void aggregate(ScheduleKTNode tree) {
                tree.residualCapacityBeforeEntry(vehicle.getResidualCapacity());
                // ?????????? ???? ?????????????? ?????????????? ???? + ?????????? ???? ?????????????????????? ??????????
                double timeOnCurrentRoad = (1 - vehicle.getProgressOnRoadSegment()) * vehicle.getRoadSegment().getCost();
                double timeToCheckpoint =
                        shortestRouteSolver.getShortestRoute(vehicle.getRoadSegment().getEndNode().getId(), tree.value().getNodeId()).getRouteCost();
                tree.bestEntryTime(Instant.now().plusSeconds((long) (timeOnCurrentRoad + timeToCheckpoint)));
            }
        });

        // ?????????????? ???????? ???? ???????? ???? ?????????????????? ????????????
        var scheduleEntries = listSchedule.stream().collect(Collectors.groupingBy(ScheduleEntry::getOrderId));
        scheduleEntries.forEach((orderId, entries) -> {
            if (entries.size() == 1) {
                kineticTree.insert(entries.get(0));
            } else if (entries.size() == 2) {
                var pickup = entries.stream()
                        .filter(entry -> entry.getKind() == ScheduleEntryKind.PICKUP)
                        .findAny().orElseThrow(() -> new IllegalStateException("pickup schedule entry expected but cannot be found"));

                var dropOff = entries.stream()
                        .filter(entry -> entry.getKind() == ScheduleEntryKind.DROPOFF)
                        .findAny().orElseThrow(() -> new IllegalStateException("drop off schedule entry expected but cannot be found"));

                kineticTree.insert(pickup, dropOff);
            } else {
                throw new IllegalStateException("more than one schedule entries with id " + orderId + " found");
            }
        });

        var bestSchedule = kineticTree.minPermutation((a, b) -> {
            if (a.size() != b.size()) {
                throw new IllegalArgumentException("permutation sizes mismatched");
            }
            if (a.isEmpty()) {
                return 0;
            }
            return Long.compare(a.get(a.size() - 1).bestEntryTime().toEpochMilli(), b.get(b.size() - 1).bestEntryTime().toEpochMilli());
        }).orElseThrow(() -> new IllegalStateException("kinetic tree not expected to be empty"));

        return new TreeSchedule(bestSchedule, kineticTree.allPermutations());
    }

    private Optional<RequestCancellation> cancelRequestInternal(Request request, ExtendedVehicle vehicle) {
        var newScheduleEntries = vehicle.getSchedule().asList().stream()
                .filter(scheduleEntry -> !Objects.equals(scheduleEntry.getOrderId(), request.getRequestId()))
                .collect(Collectors.toList());

        var newSchedule = listScheduleAsInternalSchedule(newScheduleEntries, vehicle);

        var newRouteCheckpoints = Stream.concat(
                Stream.of(vehicle.getRoadSegment().getStartNode().getId()),
                newSchedule.asList().stream().map(ScheduleEntry::getNodeId)).collect(Collectors.toList());

        var newRoute = getRouteToCompleteSchedule(newRouteCheckpoints).getRoute().stream()
                .map(this::mapNodeToRouteNode)
                .collect(Collectors.toList());

        return Optional.of(new RequestCancellation(newSchedule, newRoute));
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
