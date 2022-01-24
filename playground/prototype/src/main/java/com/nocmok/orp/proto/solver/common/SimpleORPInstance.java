package com.nocmok.orp.proto.solver.common;

import com.nocmok.orp.proto.graph.Graph;
import com.nocmok.orp.proto.solver.ORPInstance;
import com.nocmok.orp.proto.solver.Request;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

// Модуль отвечающий за хранение состояния задачи
public class SimpleORPInstance implements ORPInstance {

    @Getter
    private Graph graph;
    @Getter
    @Setter
    private int time = 0;
    @Getter
    private List<SimpleVehicle> vehicleList;
    @Getter
    private List<Request> requestLog;

    public SimpleORPInstance(Graph graph, List<SimpleVehicle> vehicles) {
        this.graph = graph;
        this.vehicleList = new ArrayList<>(vehicles);
        this.requestLog = new ArrayList<>();
    }
}
