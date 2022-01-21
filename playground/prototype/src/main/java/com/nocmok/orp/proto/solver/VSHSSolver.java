package com.nocmok.orp.proto.solver;

import com.nocmok.orp.proto.graph.Graph;
import com.nocmok.orp.proto.pojo.GPS;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

// Vehicle Selection Hybrid Search
// Гибридный алгоритм. Полный перебор на небольших планах, жадная вставка на больших планах
public class VSHSSolver implements ORPSolver {

    private ORPInstance state;
    private ShortestPathSolver shortestPathSolver;
    private int scheduleSizeThreshold = 8;

    public VSHSSolver(ORPInstance state) {
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
    private List<ScheduleCheckpoint> getAugmentedScheduleLazy(Vehicle vehicle,
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

        var scheduleGenerator = new LazyScheduleGenerator(oldSchedule, startCheckpoint, endCheckpoint);
        while (scheduleGenerator.hasNext()) {
            var augmentedSchedule = scheduleGenerator.next();
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

        return bestAugmentedSchedule;
    }

    // Возвращает пустой список, если невозможно составить план без нарушения ограничений
    private List<ScheduleCheckpoint> getAugmentedScheduleFullSearch(Vehicle vehicle,
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

        var shuffler = new AllSchedulesGenerator(oldSchedule, startCheckpoint, endCheckpoint);

        while (shuffler.hasNext()) {
            var augmentedSchedule = shuffler.next();

            if (!checkScheduleOrderViolation(augmentedSchedule)) {
                continue;
            }

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

        var pickupCheckpoint = new ScheduleCheckpoint(request, request.getDepartureNode());
        var dropoffCheckpoint = new ScheduleCheckpoint(request, request.getArrivalNode());

        var schedule = (vehicle.getCurrentSchedule().size() >= scheduleSizeThreshold)
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

    private static class AllSchedulesGenerator implements Iterator<List<ScheduleCheckpoint>> {

        private List<ScheduleCheckpoint> schedule;
        private PermutationGenerator shuffler;

        AllSchedulesGenerator(List<ScheduleCheckpoint> schedule, ScheduleCheckpoint pickup, ScheduleCheckpoint dropoff) {
            this.schedule = new ArrayList<>(schedule);
            this.schedule.add(pickup);
            this.schedule.add(dropoff);
            this.shuffler = new PermutationGenerator(this.schedule.size());
        }

        @Override public boolean hasNext() {
            return shuffler.hasNext();
        }

        private List<ScheduleCheckpoint> sample(List<Integer> positions) {
            var permutation = new ArrayList<ScheduleCheckpoint>(schedule.size());
            for (int i = 0; i < schedule.size(); ++i) {
                permutation.add(schedule.get(positions.get(i)));
            }
            return permutation;
        }

        @Override public List<ScheduleCheckpoint> next() {
            if (!hasNext()) {
                throw new NoSuchElementException();
            }
            return sample(shuffler.next());
        }
    }

    private static class PermutationGenerator implements Iterator<List<Integer>> {

        private Iterator<List<Integer>> it;

        PermutationGenerator(int size) {
            var allPermutations = new ArrayList<List<Integer>>();
            getAllPermutations(0, size, new boolean[size], new Integer[size], allPermutations);
            this.it = allPermutations.iterator();
        }

        void getAllPermutations(int start, int size, boolean[] used, Integer[] permutation, List<List<Integer>> permutations) {
            if (start >= size) {
                permutations.add(new ArrayList<>(Arrays.asList(permutation)));
                return;
            }
            for (int i = 0; i < size; ++i) {
                if (used[i]) {
                    continue;
                }
                permutation[start] = i;
                used[i] = true;
                getAllPermutations(start + 1, size, used, permutation, permutations);
                used[i] = false;
            }
        }


        @Override public boolean hasNext() {
            return it.hasNext();
        }

        @Override public List<Integer> next() {
            if (!hasNext()) {
                throw new NoSuchElementException();
            }
            return it.next();
        }
    }

    private static class LazyScheduleGenerator implements Iterator<List<ScheduleCheckpoint>> {

        private int pickupPosition;
        private int dropoffPosition;
        private List<ScheduleCheckpoint> templateSchedule;
        private ScheduleCheckpoint pickup;
        private ScheduleCheckpoint dropoff;

        LazyScheduleGenerator(List<ScheduleCheckpoint> templateSchedule, ScheduleCheckpoint pickup, ScheduleCheckpoint dropoff) {
            this.templateSchedule = templateSchedule;
            this.pickup = pickup;
            this.dropoff = dropoff;
            this.pickupPosition = 0;
            this.dropoffPosition = 1;
        }

        @Override public boolean hasNext() {
            return pickupPosition <= templateSchedule.size();
        }

        @Override public List<ScheduleCheckpoint> next() {
            if (!hasNext()) {
                throw new NoSuchElementException();
            }
            var permutation = new ArrayList<>(templateSchedule);
            permutation.add(pickupPosition, pickup);
            permutation.add(dropoffPosition, dropoff);

            if (dropoffPosition >= templateSchedule.size() + 1) {
                ++pickupPosition;
                dropoffPosition = pickupPosition + 1;
            } else {
                ++dropoffPosition;
            }

            return permutation;
        }
    }
}
