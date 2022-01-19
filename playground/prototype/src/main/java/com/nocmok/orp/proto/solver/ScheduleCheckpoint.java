package com.nocmok.orp.proto.solver;

import lombok.Getter;

@Getter
public class ScheduleCheckpoint {
    // Нода на которой находится чекпоинт
    private int node;
    // Запрос, которому соответствует чекпоинт
    private Request request;

    public ScheduleCheckpoint(Request request, int node) {
        this.request = request;
        this.node = node;
    }

    public boolean isDepartureCheckpoint() {
        return request.getDepartureNode() == node;
    }

    // Завершает ли чекпоинт запрос
    public boolean isArrivalCheckpoint() {
        return request.getArrivalNode() == node;
    }
}
