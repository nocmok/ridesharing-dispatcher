package com.nocmok.orp.proto.solver;

import lombok.Getter;

import java.util.Objects;

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

    @Override public int hashCode() {
        return Objects.hash(node, request);
    }

    @Override public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof ScheduleCheckpoint)) {
            return false;
        }
        ScheduleCheckpoint other = (ScheduleCheckpoint) obj;
        return other.node == this.node && Objects.equals(other.request, request);
    }
}
