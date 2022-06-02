package com.nocmok.orp.proto.solver;

import com.nocmok.orp.proto.graph.Graph;

import java.util.List;

public interface ORPInstance {

    Graph getGraph();

    int getTime();

    void setTime(int time);

    List<? extends Vehicle> getVehicleList();

    List<Request> getRequestLog();
}
