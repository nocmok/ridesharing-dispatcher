package com.nocmok.orp.proto.solver;

import com.nocmok.orp.proto.pojo.GPS;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class Vehicle {

    private List<GPS> gpsLog = new ArrayList<>();
    private List<Integer> schedule = new ArrayList<>();
    private List<Request> requests = new ArrayList<>();
    // How much nodes from schedule was passed by vehicle
    private int nodesPassed = 1;

    private int capacity = 3;
    private int load;

    // Waiting for request
    // Serving
    // AFK
    private State state = State.AFK;

    // Средняя скорость беспрепятственного движения
    // Единицы измерения - м/с
    private double avgVelocity = 0;

    public Vehicle(List<Integer> schedule, List<GPS> gpsLog) {
        this.schedule = new ArrayList<>(schedule);
        this.gpsLog = new ArrayList<>(gpsLog);
    }

    public Vehicle(List<Integer> schedule, List<GPS> gpsLog, State state, double avgVelocity) {
        this.schedule = new ArrayList<>(schedule);
        this.gpsLog = new ArrayList<>(gpsLog);
        this.state = state;
        this.avgVelocity = avgVelocity;
    }

    public void incrementNodesPassed() {
        ++nodesPassed;
    }

    public enum State {
        PENDING,
        SERVING,
        AFK
    }
}
