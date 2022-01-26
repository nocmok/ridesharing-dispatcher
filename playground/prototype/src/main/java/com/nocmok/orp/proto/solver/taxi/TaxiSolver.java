package com.nocmok.orp.proto.solver.taxi;

import com.nocmok.orp.proto.graph.Graph;
import com.nocmok.orp.proto.pojo.GPS;
import com.nocmok.orp.proto.solver.Matching;
import com.nocmok.orp.proto.solver.ORPSolver;
import com.nocmok.orp.proto.solver.Request;
import com.nocmok.orp.proto.solver.Route;
import com.nocmok.orp.proto.solver.ScheduleCheckpoint;
import com.nocmok.orp.proto.solver.common.ShortestPathSolver;
import com.nocmok.orp.proto.solver.common.SimpleORPInstance;
import com.nocmok.orp.proto.solver.common.SimpleVehicle;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

// Вырожденный алгоритм, рассматривающий вместимость всех транспортных средств за 1.
// Предполагается сравнивать полноценные алгоритмы райдшеринга с алгоритмом, реализованном в этом модуле
// для вычисления сэкономленного расстояния
public class TaxiSolver implements ORPSolver {

    private SimpleORPInstance state;

    private ShortestPathSolver shortestPathSolver;

    public TaxiSolver(SimpleORPInstance state) {
        this.state = state;
        this.shortestPathSolver = new ShortestPathSolver(state.getGraph());
    }

    private double distance(GPS startPoint, GPS endPoint) {
        return Math.hypot(endPoint.x - startPoint.x, endPoint.y - startPoint.y);
    }

    private Route getRoute(List<Integer> nodes) {
        double distance = 0;
        for (int i = 1; i < nodes.size(); ++i) {
            distance += state.getGraph().getRoadCost(nodes.get(i - 1), nodes.get(i));
        }
        return new Route(new ArrayList<>(nodes), distance);
    }

    private int closestNode(Graph graph, GPS point) {
        return IntStream.range(0, graph.nNodes())
                .boxed()
                .min(Comparator.comparingDouble(node -> distance(graph.getGps(node), point)))
                .orElse(0);
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

    // Правая часть не включается
    private boolean checkTimeFrame(int time, int earlyBound, int lateBound) {
        return time >= earlyBound && time < lateBound;
    }

    private Optional<Matching> matchPendingVehicle(Request request, SimpleVehicle vehicle) {
        int vehicleNode = closestNode(state.getGraph(), vehicle.getGps());
        Route routeToClient =
                shortestPathSolver.dijkstra(vehicleNode, request.getDepartureNode());
        int timeToClient = (int) (routeToClient.getDistance() / vehicle.getAverageVelocity());

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
        // найти приблизительное время в конечной точке текущего плана
        int vehicleNextNode = vehicle.getNextNode().get();
        int vehicleLastNode = vehicle.getRoute().get(vehicle.getRoute().size() - 1);
        var currentRoute = getRoute(vehicle.getRoute());
        var routeToNewClient = shortestPathSolver.dijkstra(vehicleLastNode, request.getDepartureNode());
        int timeToClient = (int) ((distance(vehicle.getGps(), state.getGraph().getGps(vehicle.getNextNode().get())) + currentRoute.getDistance() +
                routeToNewClient.getDistance()) / vehicle.getAverageVelocity());

        if (!checkTimeFrame(state.getTime() + timeToClient, request.getEarliestDepartureTime(), request.getLatestDepartureTime())) {
            return Optional.empty();
        }

        var newClientRoute = shortestPathSolver.dijkstra(request.getDepartureNode(), request.getArrivalNode());
        var fullRoute = combineRoutes(List.of(currentRoute, routeToNewClient, newClientRoute));
        var schedule = new ArrayList<>(vehicle.getSchedule());
        schedule.add(new ScheduleCheckpoint(request, request.getDepartureNode()));
        schedule.add(new ScheduleCheckpoint(request, request.getArrivalNode()));

        return Optional.of(new Matching(vehicle, fullRoute, schedule));
    }

    @Override public Matching computeMatching(Request request) {
        // Для каждой машины, которая находится в состоянии ожидания запроса считает расстояния до точки посадки.
        // Выбирает ближайшую машину
        // Считает оптимальный маршрут для выполнения запроса

        // находим доступные мащины
        var candidateVehicles = state.getVehicleList().stream()
                .filter(vehicle -> vehicle.getState() != SimpleVehicle.State.AFK)
                .collect(Collectors.toList());

        if (candidateVehicles.isEmpty()) {
            return new Matching(Matching.DenialReason.NO_VEHICLE_NEARBY);
        }

        // считаем для каждой машины ближайшую к ней ноду
        Matching bestMatching = null;
        double bestRouteDistanceToClient = Double.POSITIVE_INFINITY;

        for (var vehicle : candidateVehicles) {
            if (vehicle.getState() == SimpleVehicle.State.PENDING) {
                var matching = matchPendingVehicle(request, vehicle);
                if (matching.isEmpty()) {
                    continue;
                }
                int vehicleNode = closestNode(state.getGraph(), vehicle.getGps());
                var routeToClient = shortestPathSolver.dijkstra(vehicleNode, request.getDepartureNode());
                if (routeToClient.getDistance() >= bestRouteDistanceToClient) {
                    continue;
                }
                bestMatching = matching.get();
                bestRouteDistanceToClient = routeToClient.getDistance();
            } else if (vehicle.getState() == SimpleVehicle.State.SERVING) {
                var matching = matchServingVehicle(request, vehicle);
                if (matching.isEmpty()) {
                    continue;
                }
                int vehicleLastNode = vehicle.getRoute().get(vehicle.getRoute().size() - 1);
                var routeToClient = shortestPathSolver.dijkstra(vehicleLastNode, request.getDepartureNode());
                if (routeToClient.getDistance() >= bestRouteDistanceToClient) {
                    continue;
                }
                bestMatching = matching.get();
                bestRouteDistanceToClient = routeToClient.getDistance();
            }
        }

        if (bestMatching == null) {
            return new Matching(Matching.DenialReason.NO_VEHICLE_NEARBY);
        }

        return bestMatching;
    }
}
