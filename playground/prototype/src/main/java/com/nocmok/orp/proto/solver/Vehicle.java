package com.nocmok.orp.proto.solver;

import com.nocmok.orp.proto.pojo.GPS;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


// GPS генератору нужен текущий gps и хотя бы две точки в, чтобы генерировать GPS
//
@Getter
public class Vehicle {

    private List<GPS> gpsLog = new ArrayList<>();
    private List<Integer> route = new ArrayList<>();
    private List<Request> requests = new ArrayList<>();
    private List<ScheduleCheckpoint> schedule = new ArrayList<>();

    // How much nodes from schedule was passed by vehicle
    @Setter
    private int nodesPassed;

    // Сколько чекпоинтов было пройдено тс
    @Setter
    private int checkpointsPassed;

    private int capacity = 3;
    private int load;
    // Waiting for request
    // Serving
    // AFK
    @Setter
    private State state;
    // Средняя скорость беспрепятственного движения
    // Единицы измерения - м/с
    private double avgVelocity = 0;

    public Vehicle(GPS gps, State state, double avgVelocity) {
        this.gpsLog = new ArrayList<>(List.of(gps));
        this.state = state;
        this.avgVelocity = avgVelocity;
    }

    public Vehicle(GPS gps) {
        this(gps, State.AFK, 0d);
    }

    // Возвращает следующую в плане контрольную точку
    public Optional<ScheduleCheckpoint> getNextCheckpoint() {
        return checkpointsPassed >= schedule.size() ? Optional.empty() : Optional.of(schedule.get(checkpointsPassed));
    }

    // Возвращает следующую в маршруте вершину
    public Optional<Integer> getNextNode() {
        return nodesPassed >= route.size() ? Optional.empty() : Optional.of(route.get(nodesPassed));
    }

    public Optional<GPS> getGPS() {
        return gpsLog.isEmpty() ? Optional.empty() : Optional.of(gpsLog.get(gpsLog.size() - 1));
    }

    public List<ScheduleCheckpoint> getCurrentSchedule() {
        return schedule.subList(checkpointsPassed, schedule.size());
    }

    public List<Integer> getCurrentRoute() {
        return route.subList(nodesPassed, route.size());
    }

    public enum State {
        PENDING,
        SERVING,
        AFK
    }
}
