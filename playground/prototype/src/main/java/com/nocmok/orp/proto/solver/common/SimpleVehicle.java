package com.nocmok.orp.proto.solver.common;

import com.nocmok.orp.proto.pojo.GPS;
import com.nocmok.orp.proto.solver.ScheduleCheckpoint;
import com.nocmok.orp.proto.solver.Vehicle;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class SimpleVehicle implements Vehicle {

    private List<GPS> gpsLog = new ArrayList<>();
    private List<Integer> route = new ArrayList<>();
    private List<ScheduleCheckpoint> schedule = new ArrayList<>();

    private int nodesPassed;
    private int checkpointsPassed;
    private int capacity;
    private Vehicle.State state;
    private double avgVelocity;

    public SimpleVehicle(GPS gps, Vehicle.State state, double avgVelocity) {
        this(gps, state, avgVelocity, 3);
    }

    public SimpleVehicle(GPS gps, Vehicle.State state, double avgVelocity, int capacity) {
        this.gpsLog = new ArrayList<>(List.of(gps));
        this.state = state;
        this.avgVelocity = avgVelocity;
        this.capacity = capacity;
    }

    @Override public List<Integer> getRoute() {
        return route.subList(nodesPassed, route.size());
    }

    @Override public List<ScheduleCheckpoint> getSchedule() {
        return schedule.subList(checkpointsPassed, schedule.size());
    }

    @Override public State getState() {
        return this.state;
    }

    @Override public int getCapacity() {
        return capacity;
    }

    @Override public void passNode(int node) {
        ++nodesPassed;
        while (getNextCheckpoint().isPresent() && getNextCheckpoint().get().getNode() == node) {
            ++checkpointsPassed;
        }
    }

    @Override public void updateGps(GPS gps) {
        gpsLog.add(gps);
    }

    @Override public void updateState(State state) {
        this.state = state;
    }

    @Override public void updateSchedule(List<ScheduleCheckpoint> schedule) {
        this.schedule = new ArrayList<>(schedule);
        this.checkpointsPassed = 0;
    }

    @Override public void updateRoute(List<Integer> route) {
        this.route = new ArrayList<>(route);
        this.nodesPassed = 0;
    }

    @Override public Optional<Integer> getNextNode() {
        return nodesPassed < route.size() ? Optional.of(route.get(nodesPassed)) : Optional.empty();
    }

    @Override public GPS getGps() {
        return gpsLog.get(gpsLog.size() - 1);
    }

    @Override public Optional<ScheduleCheckpoint> getNextCheckpoint() {
        return checkpointsPassed < schedule.size() ? Optional.of(schedule.get(checkpointsPassed)) : Optional.empty();
    }

    @Override public double getAverageVelocity() {
        return avgVelocity;
    }
}
