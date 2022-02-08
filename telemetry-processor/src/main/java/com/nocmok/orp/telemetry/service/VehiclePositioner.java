package com.nocmok.orp.telemetry.service;

import com.nocmok.orp.core_api.GCS;
import com.nocmok.orp.core_api.GraphBinding;
import com.nocmok.orp.core_api.GraphIndex;
import com.nocmok.orp.core_api.GraphNode;
import com.nocmok.orp.core_api.GraphRoad;
import com.nocmok.orp.telemetry.kafka.orp_telemetry.dto.VehicleTelemetry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class VehiclePositioner {

    private final GraphIndex graphIndex;

    @Autowired
    public VehiclePositioner(GraphIndex graphIndex) {
        this.graphIndex = graphIndex;
    }

    public GraphBinding bindVehicle(VehicleTelemetry telemetry) {
        return new GraphBinding(
                new GraphRoad(
                        new GraphNode(0, new GCS(-7.4145588E7, 4.0768E7)),
                        new GraphNode(1, new GCS(-7.4146388E7, 4.07683E7)),
                        1878.0
                ),
                0.5
        );
    }
}
