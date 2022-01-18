package com.nocmok.orp.proto.simulator;

import com.nocmok.orp.proto.graph.Graph;
import com.nocmok.orp.proto.pojo.GPS;
import com.nocmok.orp.proto.solver.Vehicle;

import java.util.List;

// Module to simulate car moving
public class VehicleGPSGenerator {

    private double distance(GPS from, GPS to) {
        return Math.hypot(from.x - to.x, from.y - to.y);
    }

    private GPS addVector(GPS point, double x, double y) {
        return new GPS(point.x + x, point.y + y);
    }

    private GPS getNextVehicleGPS(Graph graph, Vehicle vehicle, int time) {
        GPS currentGPS = vehicle.getGpsLog().size() > 0 ?
                vehicle.getGpsLog().get(vehicle.getGpsLog().size() - 1)
                : graph.getGps(vehicle.getSchedule().get(vehicle.getNodesPassed() - 1));

        double distancePassed = time * vehicle.getAvgVelocity();

        // Меняем текущее ребро тс, если тс прошло большее расстояние чем осталось на его текущем участке дороги
        while (vehicle.getNodesPassed() < vehicle.getSchedule().size() &&
                distancePassed >=
                        distance(currentGPS, graph.getGps(vehicle.getSchedule().get(vehicle.getNodesPassed())))) {
            distancePassed -=
                    distance(currentGPS, graph.getGps(vehicle.getSchedule().get(vehicle.getNodesPassed())));
            currentGPS = graph.getGps(vehicle.getSchedule().get(vehicle.getNodesPassed()));
            vehicle.incrementNodesPassed();
        }

        // Если тс не завершило маршрут, то прибавляем остаточное расстояние
        if (vehicle.getNodesPassed() < vehicle.getSchedule().size()) {
            GPS currentEdgeStartGPS = graph.getGps(vehicle.getSchedule().get(vehicle.getNodesPassed() - 1));
            GPS currentEdgeEndGPS = graph.getGps(vehicle.getSchedule().get(vehicle.getNodesPassed()));
            double currentEdgeDistance = distance(currentEdgeStartGPS, currentEdgeEndGPS);
            currentGPS = addVector(currentGPS,
                    (currentEdgeEndGPS.x - currentEdgeStartGPS.x) * distancePassed / currentEdgeDistance,
                    (currentEdgeEndGPS.y - currentEdgeStartGPS.y) * distancePassed / currentEdgeDistance);
        }

        return currentGPS;
    }

    // time - amount of time in seconds
    public void moveVehicles(Graph graph, List<Vehicle> vehicles, int time) {
        for (var vehicle : vehicles) {
            // Если тс завершило поездку, не рассматриваем его
            if (vehicle.getNodesPassed() >= vehicle.getSchedule().size()) {
                continue;
            }
            vehicle.getGpsLog().add(getNextVehicleGPS(graph, vehicle, time));
        }

    }
}
