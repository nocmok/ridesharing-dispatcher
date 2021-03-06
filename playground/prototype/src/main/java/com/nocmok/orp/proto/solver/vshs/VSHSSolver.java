package com.nocmok.orp.proto.solver.vshs;

import com.nocmok.orp.proto.graph.Graph;
import com.nocmok.orp.proto.pojo.GPS;
import com.nocmok.orp.proto.solver.Matching;
import com.nocmok.orp.proto.solver.ORPSolver;
import com.nocmok.orp.proto.solver.Request;
import com.nocmok.orp.proto.solver.Route;
import com.nocmok.orp.proto.solver.ScheduleCheckpoint;
import com.nocmok.orp.proto.solver.Vehicle;
import com.nocmok.orp.proto.solver.common.AllSchedulesGenerator;
import com.nocmok.orp.proto.solver.common.LazyScheduleGenerator;
import com.nocmok.orp.proto.solver.common.ShortestPathSolver;
import com.nocmok.orp.proto.solver.common.SimpleORPInstance;
import com.nocmok.orp.proto.solver.common.SimpleVehicle;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

// Vehicle Selection Hybrid Search
// Гибридный алгоритм. Полный перебор на небольших планах, жадная вставка на больших планах
public class VSHSSolver implements ORPSolver {

    private SimpleORPInstance state;
    private ShortestPathSolver shortestPathSolver;
    private int scheduleSizeThreshold = 6;

    public VSHSSolver(SimpleORPInstance state) {
        this.state = state;
        this.shortestPathSolver = new ShortestPathSolver(state.getGraph());
    }

    private boolean checkScheduleOrderViolation(List<ScheduleCheckpoint> schedule) {
        var dropoffs = new HashSet<Request>();
        for (int i = schedule.size() - 1; i >= 0; --i) {
            var checkpoint = schedule.get(i);
            if (checkpoint.isArrivalCheckpoint()) {
                dropoffs.add(checkpoint.getRequest());
            } else {
                if (!dropoffs.contains(checkpoint.getRequest())) {
                    return false;
                }
            }
        }
        return true;
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
            if (checkpoint.isArrivalCheckpoint()) {
                if (time > checkpoint.getRequest().getLatestArrivalTime()) {
                    return false;
                }
            } else {
                if (time > checkpoint.getRequest().getLatestDepartureTime()) {
                    return false;
                }
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
    private List<ScheduleCheckpoint> getAugmentedScheduleLazy(SimpleVehicle vehicle,
                                                              ScheduleCheckpoint startCheckpoint,
                                                              ScheduleCheckpoint endCheckpoint) {
        // Вершина с которой должны начинаться маршруты для планов.
        // В качестве начальной, берется ближайшая вершина к которой движется тс
        int startNode = vehicle.getNextNode()
                .orElseGet(() -> closestNode(state.getGraph(), vehicle.getGps()));

        // Ожидаемое время системы в момент когда тс окажется в начальной вершине
        // Используется для того, чтобы проверять нарушает ли маршрут дедлайны чекпоинтов
        int startTime = state.getTime() + (int) (distance(vehicle.getGps(), state.getGraph().getGps(startNode)) / vehicle.getAverageVelocity());

        var oldSchedule = vehicle.getSchedule();
        var oldScheduleRoute = getRouteForSchedule(startNode,
                oldSchedule.stream()
                        .map(ScheduleCheckpoint::getNode)
                        .collect(Collectors.toList()));

        var bestAugmentedSchedule = new Object() {
            List<ScheduleCheckpoint> value = Collections.<ScheduleCheckpoint>emptyList();
        };
        var bestAugmentedScheduleRoute = new Object() {
            Route value = new Route(Collections.emptyList(), Double.POSITIVE_INFINITY);
        };

        var scheduleGenerator = new LazyScheduleGenerator(oldSchedule, startCheckpoint, endCheckpoint);
        scheduleGenerator.forEachSchedule(augmentedSchedule -> {

            if (!checkCapacityViolation(vehicle.getCurrentCapacity(), augmentedSchedule)) {
                return;
            }

            if (!checkDeadlineViolation(vehicle.getAverageVelocity(), startNode, startTime, augmentedSchedule)) {
                return;
            }

            Route augmentedScheduleRoute = this.getRouteForSchedule(startNode,
                    augmentedSchedule.stream()
                            .map(ScheduleCheckpoint::getNode)
                            .collect(Collectors.toList()));

            // Если добавочное расстояние уменьшилось, то обновляем лучший план
            if (augmentedScheduleRoute.getDistance() - oldScheduleRoute.getDistance() <
                    bestAugmentedScheduleRoute.value.getDistance() - oldScheduleRoute.getDistance()) {
                bestAugmentedSchedule.value = new ArrayList<>(augmentedSchedule);
                bestAugmentedScheduleRoute.value = augmentedScheduleRoute;
            }
        });

        return bestAugmentedSchedule.value;
    }

    // Возвращает пустой список, если невозможно составить план без нарушения ограничений
    private List<ScheduleCheckpoint> getAugmentedScheduleFullSearch(SimpleVehicle vehicle,
                                                                    ScheduleCheckpoint startCheckpoint,
                                                                    ScheduleCheckpoint endCheckpoint) {
        // Вершина с которой должны начинаться маршруты для планов.
        // В качестве начальной, берется ближайшая вершина к которой движется тс
        int startNode = vehicle.getNextNode()
                .orElseGet(() -> closestNode(state.getGraph(), vehicle.getGps()));

        // Ожидаемое время системы в момент когда тс окажется в начальной вершине
        // Используется для того, чтобы проверять нарушает ли маршрут дедлайны чекпоинтов
        int startTime = state.getTime() + (int) (distance(vehicle.getGps(), state.getGraph().getGps(startNode)) / vehicle.getAverageVelocity());

        var oldSchedule = vehicle.getSchedule();
        var oldScheduleRoute = getRouteForSchedule(startNode,
                oldSchedule.stream()
                        .map(ScheduleCheckpoint::getNode)
                        .collect(Collectors.toList()));

        var bestAugmentedScheduleWrapper = new Object() {
            List<ScheduleCheckpoint> value = Collections.<ScheduleCheckpoint>emptyList();
        };
        var bestAugmentedScheduleRouteWrapper = new Object() {
            Route value = new Route(Collections.emptyList(), Double.POSITIVE_INFINITY);
        };

        var scheduleGenerator = new AllSchedulesGenerator(oldSchedule, startCheckpoint, endCheckpoint);
        scheduleGenerator.forEachSchedule(augmentedSchedule -> {
            if (!checkScheduleOrderViolation(augmentedSchedule)) {
                return;
            }

            if (!checkCapacityViolation(vehicle.getCurrentCapacity(), augmentedSchedule)) {
                return;
            }

            if (!checkDeadlineViolation(vehicle.getAverageVelocity(), startNode, startTime, augmentedSchedule)) {
                return;
            }

            Route augmentedScheduleRoute = this.getRouteForSchedule(startNode,
                    augmentedSchedule.stream()
                            .map(ScheduleCheckpoint::getNode)
                            .collect(Collectors.toList()));

            // Если добавочное расстояние уменьшилось, то обновляем лучший план
            if (augmentedScheduleRoute.getDistance() - oldScheduleRoute.getDistance() <
                    bestAugmentedScheduleRouteWrapper.value.getDistance() - oldScheduleRoute.getDistance()) {
                bestAugmentedScheduleWrapper.value = new ArrayList<>(augmentedSchedule);
                bestAugmentedScheduleRouteWrapper.value = augmentedScheduleRoute;
            }
        });

        return bestAugmentedScheduleWrapper.value;
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
    private Optional<Matching> matchPendingVehicle(Request request, SimpleVehicle vehicle) {
        if (request.getLoad() > vehicle.getCapacity()) {
            return Optional.empty();
        }
        int nextVehicleNode = closestNode(state.getGraph(), vehicle.getGps());

        var routeToClient = shortestPathSolver.dijkstra(nextVehicleNode, request.getDepartureNode());
        int timeToClient =
                (int) (distance(state.getGraph().getGps(nextVehicleNode), vehicle.getGps()) + routeToClient.getDistance() / vehicle.getAverageVelocity());
        if (!checkTimeFrame(state.getTime() + timeToClient, request.getEarliestDepartureTime(), request.getLatestDepartureTime())) {
            return Optional.empty();
        }

        var routeWithClient = shortestPathSolver.dijkstra(request.getDepartureNode(), request.getArrivalNode());

        var fullRoute = combineRoutes(List.of(routeToClient, routeWithClient));
        var schedule = List.of(new ScheduleCheckpoint(request, request.getDepartureNode()),
                new ScheduleCheckpoint(request, request.getArrivalNode()));

        return Optional.of(new Matching(vehicle, fullRoute, schedule));
    }

    private Optional<Matching> matchServingVehicle(Request request, SimpleVehicle vehicle) {
        if (request.getLoad() > vehicle.getCapacity()) {
            return Optional.empty();
        }

        int nextVehicleNode = vehicle.getNextNode().get();

        var pickupCheckpoint = new ScheduleCheckpoint(request, request.getDepartureNode());
        var dropoffCheckpoint = new ScheduleCheckpoint(request, request.getArrivalNode());

        var schedule = (vehicle.getSchedule().size() >= scheduleSizeThreshold)
                ? getAugmentedScheduleLazy(vehicle, pickupCheckpoint, dropoffCheckpoint)
                : getAugmentedScheduleFullSearch(vehicle, pickupCheckpoint, dropoffCheckpoint);

        if (schedule.isEmpty()) {
            return Optional.empty();
        }

        var route = getRouteForSchedule(nextVehicleNode,
                schedule.stream()
                        .map(ScheduleCheckpoint::getNode)
                        .collect(Collectors.toList()));

        return Optional.of(new Matching(vehicle, route, schedule));
    }

    private List<SimpleVehicle> filterVehicleCandidates(Request request) {
        var candidates = new ArrayList<SimpleVehicle>();
        for (var vehicle : state.getVehicleList()) {
            if (vehicle.getState() == Vehicle.State.AFK) {
                continue;
            }
            double distanceToClient;
            if (vehicle.getNextNode().isPresent()) {
                distanceToClient = distance(vehicle.getGps(), state.getGraph().getGps(vehicle.getNextNode().get()))
                        + shortestPathSolver.dijkstra(vehicle.getNextNode().get(), request.getDepartureNode()).getDistance();
            } else {
                int nextNode = closestNode(state.getGraph(), vehicle.getGps());
                distanceToClient = distance(vehicle.getGps(), state.getGraph().getGps(nextNode))
                        + shortestPathSolver.dijkstra(nextNode, request.getDepartureNode()).getDistance();

            }
            if (state.getTime() + distanceToClient / vehicle.getAverageVelocity() > request.getLatestDepartureTime()) {
                continue;
            }
            candidates.add(vehicle);
        }
        return candidates;
    }

    @Override public Matching computeMatching(Request request) {

        var candidateVehicles = filterVehicleCandidates(request);

        if (candidateVehicles.isEmpty()) {
            return new Matching(Matching.DenialReason.NO_VEHICLE_NEARBY);
        }

        Matching bestMatching = null;
        double bestScheduleDistanceLag = Double.POSITIVE_INFINITY;

        for (var vehicle : candidateVehicles) {
            // Тс в данный момент не выполняет план
            if (vehicle.getState() == SimpleVehicle.State.PENDING) {
                var matching = matchPendingVehicle(request, vehicle);
                if (matching.isEmpty()) {
                    continue;
                }
                if (matching.get().getRoute().getDistance() < bestScheduleDistanceLag) {
                    bestMatching = matching.get();
                    bestScheduleDistanceLag = matching.get().getRoute().getDistance();
                }
            } else if (vehicle.getState() == SimpleVehicle.State.SERVING) {
                int nextVehicleNode = vehicle.getNextNode().get();
                var oldSchedule = vehicle.getSchedule();
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
