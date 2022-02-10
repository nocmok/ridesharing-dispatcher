package com.nocmok.orp.core_api;

import java.util.List;

public interface Vehicle {

    /**
     * Идентификатор тс
     */
    String getId();

    /**
     * Текущий статус тс
     */
    VehicleStatus getStatus();

    /**
     * Для обновления статуса тс
     */
    void setStatus(VehicleStatus status);

    /**
     * Текущий план тс
     */
    List<ScheduleNode> getSchedule();

    /**
     * Для обновления плана тс
     */
    void setSchedule(List<ScheduleNode> schedule);

    /**
     * Суммарная емкость тс
     */
    Integer getCapacity();

    /**
     * Свободная емкость тс
     */
    Integer getResidualCapacity();

    /**
     * Для обновления свободной емкости тс
     */
    void setResidualCapacity(int capacity);

    /**
     * Текущие координаты тс
     */
    GCS getGCS();

    /**
     * Возвращает привязку тс к графу
     */
    GraphBinding getRoadBinding();

    /**
     * Возвращает расстояние запланированного маршрута.
     * Если тс находится в состоянии PENDING, то это значение = 0.
     * Если тс выполняет план, то это значение равно сумме весов оставшихся в маршруте ребер, кроме текущего
     */
    Double getDistanceScheduled();
}
