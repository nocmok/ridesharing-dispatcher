package com.nocmok.orp.telemetry.tracker;

import com.nocmok.orp.core_api.GCS;
import com.nocmok.orp.core_api.GraphBinding;
import com.nocmok.orp.core_api.GraphRoad;

import java.util.List;

public class VotingTracker implements VehicleTracker {

    @Override public List<GraphRoad> matchTrackToGraph(List<GCS> track) {
        throw new UnsupportedOperationException("not implemented");
    }

    @Override public GraphBinding getBinding(GraphRoad roadToBind, GCS gcs) {
        throw new UnsupportedOperationException("not implemented");
    }
}
