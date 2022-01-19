package com.nocmok.orp.proto.solver;

import com.nocmok.orp.proto.graph.Graph;
import com.nocmok.orp.proto.pojo.GPS;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

// Вырожденный алгоритм, рассматривающий вместимость всех транспортных средств за 1.
// Предполагается сравнивать полноценные алгоритмы райдшеринга с алгоритмом, реализованном в этом модуле
// для вычисления сэкономленного расстояния
public class TaxiSolver implements ORPSolver {

    private ORPInstance state;

    private ShortestPathSolver shortestPathSolver = new ShortestPathSolver();

    public TaxiSolver(ORPInstance state) {
        this.state = state;
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

    @Override public Matching computeMatching(Request request) {
        // Для каждой машины, которая находится в состоянии ожидания запроса считает расстояния до точки посадки.
        // Выбирает ближайшую машину
        // Считает оптимальный маршрут для выполнения запроса

        // находим доступные мащины
        var pendingVehicles = state.getVehicleList().stream()
                .filter(vehicle -> vehicle.getState() == Vehicle.State.PENDING)
                .collect(Collectors.toList());

        if (pendingVehicles.isEmpty()) {
            return new Matching(Matching.DenialReason.NO_VEHICLE_NEARBY);
        }

        // считаем для каждой машины ближайшую к ней ноду
        Vehicle bestVehicle = null;
        Route bestRouteToClient = new Route(Collections.emptyList(), Double.POSITIVE_INFINITY);

        for (var vehicle : pendingVehicles) {
            int vehicleNode = closestNode(state.getGraph(), vehicle.getGpsLog().get(vehicle.getGpsLog().size() - 1));
            Route routeToClient =
                    shortestPathSolver.dijkstra(state.getGraph(), vehicleNode, request.getDepartureNode());
            int timeToClient = (int) (routeToClient.getDistance() / vehicle.getAvgVelocity());

            if (routeToClient.getDistance() < bestRouteToClient.getDistance()) {
                bestVehicle = vehicle;
                bestRouteToClient = routeToClient;
            }

            // проверяем временные рамки клиента
            if (!checkTimeFrame(state.getTime() + timeToClient, request.getDepartureTimeWindow()[0],
                    request.getDepartureTimeWindow()[1])) {
                continue;
            }
        }

        if (bestVehicle == null) {
            return new Matching(Matching.DenialReason.NO_VEHICLE_NEARBY);
        }

        if (bestRouteToClient.getDistance() == Double.POSITIVE_INFINITY) {
            return new Matching(Matching.DenialReason.OUT_OF_SERVICE_REGION);
        }

        // Лучший маршрут от точки посадки до точки прибытия клиента
        Route routeWithClient =
                shortestPathSolver.dijkstra(state.getGraph(), request.getDepartureNode(), request.getArrivalNode());
        var completeRoute = new ArrayList<Integer>();
        completeRoute.addAll(bestRouteToClient.getRoute().subList(0, bestRouteToClient.getRoute().size() - 1));
        completeRoute.addAll(routeWithClient.getRoute());
        double compleRouteDistance = bestRouteToClient.getDistance() + routeWithClient.getDistance();

        List<ScheduleCheckpoint> schedule = new ArrayList<>();
        schedule.add(new ScheduleCheckpoint(request, request.getDepartureNode()));
        schedule.add(new ScheduleCheckpoint(request, request.getArrivalNode()));

        return new Matching(bestVehicle, new Route(completeRoute, compleRouteDistance), schedule);
    }
}
