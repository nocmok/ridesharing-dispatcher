package com.nocmok.orp.proto.simulator;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Metrics {

    private double totalDistance;
    private int totalRequests;
    private int deniedRequests;

    @Override public String toString() {
        return "totalDistance=" + totalDistance +
                "\ntotalRequests=" + totalRequests +
                "\ndeniedRequests=" + deniedRequests;
    }
}
