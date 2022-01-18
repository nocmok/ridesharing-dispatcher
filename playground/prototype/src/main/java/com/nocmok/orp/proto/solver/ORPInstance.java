package com.nocmok.orp.proto.solver;

import com.nocmok.orp.proto.graph.Graph;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

// Модуль отвечающий за хранение состояния задачи
@Getter
@Setter
public class ORPInstance {

    private Graph graph;
    private int time = 0;
    private List<Vehicle> vehicleList;
    private List<Request> requestLog;

    public ORPInstance(Graph graph, List<Vehicle> vehicles) {
        this.graph = graph;
        this.vehicleList = new ArrayList<>(vehicles);
        this.requestLog = new ArrayList<>();
    }
}
