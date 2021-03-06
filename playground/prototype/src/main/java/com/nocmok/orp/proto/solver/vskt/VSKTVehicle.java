package com.nocmok.orp.proto.solver.vskt;

import com.nocmok.orp.proto.pojo.GPS;
import com.nocmok.orp.proto.solver.Request;
import com.nocmok.orp.proto.solver.ScheduleCheckpoint;
import com.nocmok.orp.proto.solver.Vehicle;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class VSKTVehicle implements Vehicle {

    private List<GPS> gpsLog = new ArrayList<>();
    private List<Integer> route = new ArrayList<>();
    private List<ScheduleCheckpoint> schedule = new ArrayList<>();
    private ScheduleTree scheduleTree;

    private int nodesPassed;
    private int checkpointsPassed;
    private int capacity;
    private int currentCapacity;
    private Vehicle.State state;
    private double avgVelocity;

    public VSKTVehicle(GPS gps, Vehicle.State state, double avgVelocity, ScheduleTreeFabric scheduleTreeFabric) {
        this(gps, state, avgVelocity, 3, scheduleTreeFabric);
    }

    public VSKTVehicle(GPS gps, Vehicle.State state, double avgVelocity, int capacity, ScheduleTreeFabric scheduleTreeFabric) {
        this.gpsLog = new ArrayList<>(List.of(gps));
        this.state = state;
        this.avgVelocity = avgVelocity;
        this.capacity = capacity;
        this.currentCapacity = capacity;
        this.scheduleTree = scheduleTreeFabric.createScheduleTree(this);
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
        while(getNextNode().isPresent() && Objects.equals(getNextNode().get(), node)) {
            ++nodesPassed;
        }
        while (getNextCheckpoint().isPresent() && Objects.equals(getNextCheckpoint().get().getNode(), node)) {
            scheduleTree.passCheckpoint(getNextCheckpoint().get());
            if (getNextCheckpoint().get().isArrivalCheckpoint()) {
                currentCapacity += getNextCheckpoint().get().getRequest().getLoad();
            } else {
                currentCapacity -= getNextCheckpoint().get().getRequest().getLoad();
            }
            ++checkpointsPassed;
        }
    }

    @Override public void updateGps(GPS gps) {
        gpsLog.add(gps);
    }

    @Override public void updateState(State state) {
        this.state = state;
    }

    // ?????????????????? ?????????????????????? ?????????? ???? ???????? ??????????, ?????????????? ?????????????????? ?? ???????????? ??????????????
    // ???????? ???????????? ?????????? 1, ???????????? ?? ?????????? ?????? ????????
    private List<ScheduleCheckpoint[]> getCoupledCheckpoints(List<ScheduleCheckpoint> schedule) {
        var requestToCheckpoint = new HashMap<Request, List<ScheduleCheckpoint>>();
        for (var chekpoint : schedule) {
            requestToCheckpoint.computeIfAbsent(chekpoint.getRequest(), (r) -> new ArrayList<>())
                    .add(chekpoint);
        }
        var coupledCheckpoints = new ArrayList<ScheduleCheckpoint[]>();
        for (var pair : requestToCheckpoint.values()) {
            coupledCheckpoints.add(pair.toArray(new ScheduleCheckpoint[0]));
        }
        return coupledCheckpoints;
    }

    @Override public void updateSchedule(List<ScheduleCheckpoint> newSchedule) {
        var newScheduleSet = new HashSet<>(newSchedule);
        // ???????? ?? ?????????????? ?????????? ???????? ????????, ?????????????? ?????? ?? ?????????? ?????????? - ?????????????????????????? ???????????? ??????????????
        if (!newScheduleSet.containsAll(getSchedule())) {
            scheduleTree.clear();
            for (var checkpointPair : getCoupledCheckpoints(newSchedule)) {
                if (checkpointPair.length == 1) {
                    scheduleTree.insertDropoffCheckpoint(checkpointPair[0]);
                } else if (checkpointPair.length == 2) {
                    scheduleTree.insert(checkpointPair[0], checkpointPair[1]);
                } else {
                    throw new RuntimeException(
                            "expected neither one checkpoint neither checkpoint pair, but " + checkpointPair.length + " checkpoints provided");
                }
            }
        } else {
            // ?????????? ??????????, ?????????????? ?????? ?? ?????????????? ??????????
            var scheduleSet = new HashSet<>(getSchedule());

            var newCheckpoints = newSchedule.stream()
                    .filter(Predicate.not(scheduleSet::contains))
                    .collect(Collectors.toList());

            // ??????????????????, ?????? ?????????? ?????????? ???????????? ??????????
            if ((newCheckpoints.size() & 1) != 0) {
                throw new RuntimeException("new schedule should contain even number of new checkpoints");
            }

            // ?????????????? ???? ???????? ?? ????????????????
            for (var checkpointPair : getCoupledCheckpoints(newCheckpoints)) {
                if (checkpointPair.length != 2) {
                    throw new RuntimeException("new checkpoints should be coupled pickup -> dropoff checkpoints");
                }
                scheduleTree.insert(checkpointPair[0], checkpointPair[1]);
            }
        }
        this.schedule = new ArrayList<>(newSchedule);

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

    public ScheduleTree getScheduleTree() {
        return scheduleTree;
    }

    @Override public int getCurrentCapacity() {
        return currentCapacity;
    }

    @Override public void setCurrentCapacity(int value) {
        this.currentCapacity = value;
    }
}
