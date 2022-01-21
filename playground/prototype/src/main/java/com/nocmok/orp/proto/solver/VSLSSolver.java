package com.nocmok.orp.proto.solver;

import com.nocmok.orp.proto.graph.Graph;
import com.nocmok.orp.proto.pojo.GPS;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

// VSSS - Vehicle Selection Lazy Search
// Ленивый перебор планов. Контрольные точки из старого плана не меняют относительного порядка
public class VSLSSolver implements ORPSolver {

    private ORPInstance state;
    private ShortestPathSolver shortestPathSolver;

    public VSLSSolver(ORPInstance state) {
        this.state = state;
        this.shortestPathSolver = new ShortestPathSolver(state.getGraph());
    }

    // Проверяет валиден ли план с точки зрения соблюдения ограничений на вместимость тс
    private boolean checkCapacityViolation(int initialCapacity,
                                           List<ScheduleCheckpoint> schedule) {
        int capacity = initialCapacity;
        for (var checkpoint : schedule) {
            if (checkpoint.isArrivalCheckpoint()) {
                capacity += checkpoint.getRequest().getLoad();
            } else {
                capacity -= checkpoint.getRequest().getLoad();
            }
            if (capacity < 0) {
                return false;
            }
        }
        return true;
    }

    // Проверяет валиден ли план с точки зрения соблюдения дедлайнов прибытия в контрольные точки
    private boolean checkDeadlineViolation(double avgVelocity, int startNode, int startTime,
                                           List<ScheduleCheckpoint> schedule) {
        double time = startTime;
        int prevNode = startNode;

        for (var checkpoint : schedule) {
            Route route = shortestPathSolver.dijkstra(prevNode, checkpoint.getNode());
            time += route.getDistance() / avgVelocity;
            if (time > checkpoint.getRequest().getArrivalTimeWindow()[1]) {
                return false;
            }
            prevNode = checkpoint.getNode();
        }

        return true;
    }

    // Строит оптимальный маршрут для выполнения плана
    // startNode - вершина из которой строится маршрут, проходящий по порядку все точки из плана.
    // schedule - контрольные точки в порядке, в котором они должны быть посещены
    private Route getRouteForSchedule(int startNode, List<Integer> schedule) {
        List<Integer> nodes = new ArrayList<Integer>();
        double distance = 0;
        int prevNode = startNode;
        for (var checkpoint : schedule) {
            Route route = shortestPathSolver.dijkstra(prevNode, checkpoint);
            // удаляем дублирующуюся вершины
            if (!nodes.isEmpty()) {
                nodes.remove(nodes.size() - 1);
            }
            nodes.addAll(route.getRoute());
            distance += route.getDistance();
            prevNode = checkpoint;
        }
        return new Route(nodes, distance);
    }

    // Возвращает пустой список, если невозможно составить план без нарушения ограничений
    private List<ScheduleCheckpoint> computeBestAugmentedSchedule(Vehicle vehicle,
                                                                  ScheduleCheckpoint startCheckpoint,
                                                                  ScheduleCheckpoint endCheckpoint) {
        // Вершина с которой должны начинаться маршруты для планов.
        // В качестве начальной, берется ближайшая вершина к которой движется тс
        int startNode = vehicle.getNextNode()
                .orElseGet(() -> vehicle.getRoute().get(vehicle.getNodesPassed() - 1));

        // Ожидаемое время системы в момент когда тс окажется в начальной вершине
        // Используется для того, чтобы проверять нарушает ли маршрут дедлайны чекпоинтов
        int startTime = state.getTime() + (int) (distance(vehicle.getGPS().get(), state.getGraph().getGps(startNode)) / vehicle.getAvgVelocity());

        var oldSchedule = vehicle.getCurrentSchedule();
        var oldScheduleRoute = getRouteForSchedule(startNode,
                oldSchedule.stream()
                        .map(ScheduleCheckpoint::getNode)
                        .collect(Collectors.toList()));

        var bestAugmentedSchedule = Collections.<ScheduleCheckpoint>emptyList();
        var bestAugmentedScheduleRoute = new Route(Collections.emptyList(), Double.POSITIVE_INFINITY);

        for (int start = 0; start <= oldSchedule.size(); ++start) {
            for (int end = start + 1; end <= oldSchedule.size() + 1; ++end) {
                var augmentedSchedule = new ArrayList<>(oldSchedule);
                augmentedSchedule.add(start, startCheckpoint);
                augmentedSchedule.add(end, endCheckpoint);

                if (!checkCapacityViolation(vehicle.getCapacity(), augmentedSchedule)) {
                    continue;
                }

                if (!checkDeadlineViolation(vehicle.getAvgVelocity(), startNode, startTime, augmentedSchedule)) {
                    continue;
                }

                Route augmentedScheduleRoute = this.getRouteForSchedule(startNode,
                        augmentedSchedule.stream()
                                .map(ScheduleCheckpoint::getNode)
                                .collect(Collectors.toList()));

                // Если добавочное расстояние уменьшилось, то обновляем лучший план
                if (augmentedScheduleRoute.getDistance() - oldScheduleRoute.getDistance() <
                        bestAugmentedScheduleRoute.getDistance() - oldScheduleRoute.getDistance()) {
                    bestAugmentedSchedule = augmentedSchedule;
                    bestAugmentedScheduleRoute = augmentedScheduleRoute;
                }
            }
        }

        return bestAugmentedSchedule;
    }

    private double distance(GPS startPoint, GPS endPoint) {
        return Math.hypot(endPoint.x - startPoint.x, endPoint.y - startPoint.y);
    }

    private int closestNode(Graph graph, GPS point) {
        return IntStream.range(0, graph.nNodes())
                .boxed()
                .min(Comparator.comparingDouble(node -> distance(graph.getGps(node), point)))
                .orElse(0);
    }

    // Правая часть не включается
    private boolean checkTimeFrame(int time, int earlyBound, int lateBound) {
        return time >= earlyBound && time < lateBound;
    }

    // Объединяет связанные маршруты.
    // Последний элемент одного маршрута должен совпадать с первым элементом следующего
    private Route combineRoutes(List<Route> routes) {
        List<Integer> combinedRoute = new ArrayList<>();
        double combinedRouteDistance = 0;
        for (var route : routes) {
            if (!combinedRoute.isEmpty()) {
                if (!Objects.equals(combinedRoute.get(combinedRoute.size() - 1), route.getRoute().get(0))) {
                    throw new RuntimeException("routes expected to follow each other, " +
                            "but last node of preceding route was not equal to first node of the following route");
                }
                combinedRoute.remove(combinedRoute.size() - 1);
            }
            combinedRoute.addAll(route.getRoute());
            combinedRouteDistance += route.getDistance();
        }
        return new Route(combinedRoute, combinedRouteDistance);
    }

    // Возвращает null если временные ограничения запроса не могут быть выполнены
    private Optional<Matching> matchPendingVehicle(Request request, Vehicle vehicle) {
        if (request.getLoad() > vehicle.getCapacity()) {
            return Optional.empty();
        }
        int nextVehicleNode = closestNode(state.getGraph(), vehicle.getGPS().get());

        var routeToClient = shortestPathSolver.dijkstra(nextVehicleNode, request.getDepartureNode());
        int timeToClient =
                (int) (distance(state.getGraph().getGps(nextVehicleNode), vehicle.getGPS().get()) + routeToClient.getDistance() / vehicle.getAvgVelocity());
        if (!checkTimeFrame(state.getTime() + timeToClient, request.getEarliestDepartureTime(), request.getLatestDepartureTime())) {
            return Optional.empty();
        }

        var routeWithClient = shortestPathSolver.dijkstra(request.getDepartureNode(), request.getArrivalNode());

        var fullRoute = combineRoutes(List.of(routeToClient, routeWithClient));
        var schedule = List.of(new ScheduleCheckpoint(request, request.getDepartureNode()),
                new ScheduleCheckpoint(request, request.getArrivalNode()));

        return Optional.of(new Matching(vehicle, fullRoute, schedule));
    }

    private Optional<Matching> matchServingVehicle(Request request, Vehicle vehicle) {
        if (request.getLoad() > vehicle.getCapacity()) {
            return Optional.empty();
        }

        int nextVehicleNode = vehicle.getNextNode().get();

        var schedule = computeBestAugmentedSchedule(vehicle,
                new ScheduleCheckpoint(request, request.getDepartureNode()),
                new ScheduleCheckpoint(request, request.getArrivalNode()));

        if (schedule.isEmpty()) {
            return Optional.empty();
        }

        var route = getRouteForSchedule(nextVehicleNode,
                schedule.stream()
                        .map(ScheduleCheckpoint::getNode)
                        .collect(Collectors.toList()));

        return Optional.of(new Matching(vehicle, route, schedule));
    }

    @Override public Matching computeMatching(Request request) {

        var candidateVehicles = state.getVehicleList().stream()
                .filter(vehicle -> vehicle.getState() != Vehicle.State.AFK)
                .collect(Collectors.toCollection(ArrayList::new));

        if (candidateVehicles.isEmpty()) {
            return new Matching(Matching.DenialReason.NO_VEHICLE_NEARBY);
        }

        Matching bestMatching = null;
        double bestScheduleDistanceLag = Double.POSITIVE_INFINITY;

        for (var vehicle : candidateVehicles) {
            // Тс в данный момент не выполняет план
            if (vehicle.getState() == Vehicle.State.PENDING) {
                var matching = matchPendingVehicle(request, vehicle);
                if (matching.isEmpty()) {
                    continue;
                }
                if (matching.get().getRoute().getDistance() < bestScheduleDistanceLag) {
                    bestMatching = matching.get();
                    bestScheduleDistanceLag = matching.get().getRoute().getDistance();
                }
            } else if (vehicle.getState() == Vehicle.State.SERVING) {
                int nextVehicleNode = vehicle.getNextNode().get();
                var oldSchedule = vehicle.getCurrentSchedule();
                var oldScheduleRoute = getRouteForSchedule(nextVehicleNode,
                        oldSchedule.stream()
                                .map(ScheduleCheckpoint::getNode)
                                .collect(Collectors.toList()));

                var matching = matchServingVehicle(request, vehicle);
                if (matching.isEmpty()) {
                    continue;
                }
                if (matching.get().getRoute().getDistance() - oldScheduleRoute.getDistance() < bestScheduleDistanceLag) {
                    bestMatching = matching.get();
                    bestScheduleDistanceLag = matching.get().getRoute().getDistance() - oldScheduleRoute.getDistance();
                }
            }
        }

        if (bestMatching == null) {
            return new Matching(Matching.DenialReason.NO_VEHICLE_NEARBY);
        }

        return bestMatching;
    }
}
