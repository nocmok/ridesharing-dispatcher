package com.nocmok.orp.proto.solver;

import com.nocmok.orp.proto.graph.Graph;
import com.nocmok.orp.proto.pojo.GPS;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

// Вырожденный алгоритм, рассматривающий вместимость всех транспортных средств за 1.
// Предполагается сравнивать полноценные алгоритмы райдшеринга с алгоритмом, реализованном в этом модуле
// для вычисления сэкономленного расстояния
public class TaxiSolver implements ORPSolver {

    private ORPInstance state;

    public TaxiSolver(ORPInstance state) {
        this.state = state;
    }

    private Route dijkstra(Graph graph, int startNode, int endNode) {
        // есть множество просмотренных вершин
        // проходимся по всем вершинам, в которые можно попасть из просмотренных и выбираем ближайшую
        // добавляем вершину в множество
        // релаксируем связанные вершины

        double[] bestDistances = new double[graph.nNodes()];
        Arrays.fill(bestDistances, Double.POSITIVE_INFINITY);

        boolean[] used = new boolean[graph.nNodes()];
        var candidateNodes = new HashSet<Integer>();
        int[] prevNode = new int[graph.nNodes()];

        bestDistances[startNode] = 0;
        used[startNode] = true;
        prevNode[startNode] = startNode;

        for (var node : graph.getLinkedNodes(startNode)) {
            if (used[node]) {
                continue;
            }
            candidateNodes.add(node);
            bestDistances[node] = graph.getRoadCost(startNode, node);
            prevNode[node] = startNode;
        }

        for (int k = 2; k < graph.nNodes(); ++k) {
            int nextNode = -1;
            double nextNodeDistance = Double.POSITIVE_INFINITY;

            for (var node : candidateNodes) {
                if (bestDistances[node] < nextNodeDistance) {
                    nextNode = node;
                    nextNodeDistance = bestDistances[node];
                }
            }

            // Граф несвязанный
            if (nextNode == -1) {
                break;
            }

            // Нашли нужную вершину
            if (nextNode == endNode) {
                break;
            }

            // Релаксация
            for (var node : graph.getLinkedNodes(nextNode)) {
                if (used[node]) {
                    continue;
                }
                candidateNodes.add(node);
                if (bestDistances[nextNode] + graph.getRoadCost(nextNode, node) < bestDistances[node]) {
                    bestDistances[node] = bestDistances[nextNode] + graph.getRoadCost(nextNode, node);
                    prevNode[node] = nextNode;
                }
            }

            used[nextNode] = true;
            candidateNodes.remove(nextNode);
        }

        if (bestDistances[endNode] == Double.POSITIVE_INFINITY) {
            return new Route(Collections.emptyList(), Double.POSITIVE_INFINITY);
        }

        var route = new ArrayDeque<Integer>();
        int node = endNode;
        for (int i = 0; i < graph.nNodes(); ++i) {
            route.offerFirst(node);
            if (node == startNode) {
                break;
            }
            node = prevNode[node];
        }

        return new Route(new ArrayList<>(route), bestDistances[endNode]);
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

        int departureNode = closestNode(state.getGraph(), request.getDeparturePoint());
        int arrivalNode = closestNode(state.getGraph(), request.getArrivalPoint());

        for (var vehicle : pendingVehicles) {
            int vehicleNode = closestNode(state.getGraph(), vehicle.getGpsLog().get(vehicle.getGpsLog().size() - 1));
            Route routeToClient = dijkstra(state.getGraph(), vehicleNode, departureNode);
            int timeToClient = (int) (routeToClient.distance / vehicle.getAvgVelocity());

            if (routeToClient.distance < bestRouteToClient.distance) {
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

        if (bestRouteToClient.distance == Double.POSITIVE_INFINITY) {
            return new Matching(Matching.DenialReason.OUT_OF_SERVICE_REGION);
        }

        // Лучший маршрут от точки посадки до точки прибытия клиента
        Route routeWithClient = dijkstra(state.getGraph(), departureNode, arrivalNode);
        var completeRoute = new ArrayList<Integer>();
        completeRoute.addAll(bestRouteToClient.route.subList(0, bestRouteToClient.route.size() - 1));
        completeRoute.addAll(routeWithClient.route);
        double compleRouteDistance = bestRouteToClient.distance + routeWithClient.distance;

        return new Matching(bestVehicle, completeRoute, compleRouteDistance);
    }

    private static class Route {
        double distance;
        List<Integer> route;

        Route(List<Integer> route, double distance) {
            this.route = route;
            this.distance = distance;
        }
    }
}
