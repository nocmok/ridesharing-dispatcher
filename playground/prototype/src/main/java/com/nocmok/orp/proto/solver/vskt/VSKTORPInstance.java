package com.nocmok.orp.proto.solver.vskt;

import com.nocmok.orp.proto.graph.Graph;
import com.nocmok.orp.proto.solver.ORPInstance;
import com.nocmok.orp.proto.solver.Request;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

public class VSKTORPInstance implements ORPInstance {

    @Getter
    private Graph graph;
    @Getter
    @Setter
    private int time = 0;
    @Getter
    private List<VSKTVehicle> vehicleList;
    @Getter
    private List<Request> requestLog;

    public VSKTORPInstance(Graph graph, List<VSKTVehicle> vehicles) {
        this.graph = graph;
        this.vehicleList = new ArrayList<>(vehicles);
        this.requestLog = new ArrayList<>();
    }

    public VSKTORPInstance(Graph graph) {
        this.graph = graph;
        this.vehicleList = new ArrayList<>();
        this.requestLog = new ArrayList<>();
    }

    public void addVehicle(VSKTVehicle vehicle) {
        this.vehicleList.add(vehicle);
    }
}
