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
     * Возвращает запланированный маршрут тс включая текущее ребро
     */
    List<GraphNode> getRouteScheduled();
}
