package com.nocmok.orp.proto.solver;

import com.nocmok.orp.proto.pojo.GPS;
import com.nocmok.orp.proto.solver.common.SimpleVehicle;

import java.util.List;
import java.util.Optional;

public interface Vehicle {

    // Вызывается, когда тс проходит ноду графа
    void passNode(int node);

    // Этот метод не нужен, так как тс может само трекать пройденные чекпоинты в методе passCheckpoint
    // void passCheckpoint();

    // Обновляет координату тс
    void updateGps(GPS gps);

    // Обновляет состояние тс
    void updateState(SimpleVehicle.State state);

    State getState();

    void updateSchedule(List<ScheduleCheckpoint> schedule);

    void updateRoute(List<Integer> route);

    List<Integer> getRoute();

    // Следующая в маршруте вершина
    Optional<Integer> getNextNode();

    GPS getGps();

    List<ScheduleCheckpoint> getSchedule();

    Optional<ScheduleCheckpoint> getNextCheckpoint();

    double getAverageVelocity();

    int getCapacity();

    int getCurrentCapacity();

    void setCurrentCapacity(int value);

    public enum State {
        PENDING,
        SERVING,
        AFK
    }
}
