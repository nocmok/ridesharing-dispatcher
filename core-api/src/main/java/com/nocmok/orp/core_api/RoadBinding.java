package com.nocmok.orp.core_api;

/**
 * Для привязывания тс к дорожному графу
 */
public class RoadBinding {

    /**
     * Привязка к ребру графа
     */
    private final Road road;

    /**
     * Положение внутри ребра.
     * Значение 0 соответствует road.startNode
     * Значение 1 соответствует road.endNode
     */
    private final double progress;

    public RoadBinding(Road road, double progress) {
        this.road = road;
        this.progress = progress;
    }

    public Road getRoad() {
        return road;
    }

    public double getProgress() {
        return progress;
    }

    @Override public String toString() {
        return "RoadBinding{" +
                "road=" + road +
                ", progress=" + progress +
                '}';
    }
}
