package com.nocmok.orp.proto.simulator;

import com.nocmok.orp.proto.graph.Graph;
import com.nocmok.orp.proto.pojo.GPS;
import com.nocmok.orp.proto.solver.Vehicle;
import lombok.Getter;

import java.util.List;

// Module to simulate car moving
public class VehicleGPSGenerator {

    private double distance(GPS from, GPS to) {
        return Math.hypot(from.x - to.x, from.y - to.y);
    }

    private GPS addVector(GPS point, double x, double y) {
        return new GPS(point.x + x, point.y + y);
    }

    // Возвращает положение тс через time секунд
    public Position getNextVehicleGPS(Graph graph, Vehicle vehicle, int time) {
        GPS currentGPS = vehicle.getGps();

        double distancePassed = time * vehicle.getAverageVelocity();
        int nodesPassed = 0;
        List<Integer> route = vehicle.getRoute();

        for (; nodesPassed < route.size(); ++nodesPassed) {
            if (distancePassed < distance(currentGPS, graph.getGps(route.get(nodesPassed)))) {
                break;
            }
            distancePassed -= distance(currentGPS, graph.getGps(route.get(nodesPassed)));
            currentGPS = graph.getGps(route.get(nodesPassed));
        }

        // Если тс не завершило маршрут, то прибавляем остаточное расстояние
        if (nodesPassed < route.size()) {
            GPS nextGPS = graph.getGps(route.get(nodesPassed));
            currentGPS =
                    addVector(currentGPS, (nextGPS.x - currentGPS.x) * distancePassed / distance(currentGPS, nextGPS),
                            (nextGPS.y - currentGPS.y) * distancePassed / distance(currentGPS, nextGPS));
        }

        return new Position(currentGPS, nodesPassed);
    }

    @Getter
    public static class Position {

        private GPS gps;
        // Количество вершин, которое было пройдено тс за такт
        private int nodesPassed;

        private Position(GPS position, int nodesPassed) {
            this.gps = position;
            this.nodesPassed = nodesPassed;
        }
    }
}
