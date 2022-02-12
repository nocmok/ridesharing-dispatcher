package com.nocmok.orp.state_keeper.pg;

import com.nocmok.orp.core_api.GCS;
import com.nocmok.orp.core_api.GraphBinding;

public class Geotag {

    private GraphBinding graphBinding;
    private GCS gcs;

    public Geotag(GraphBinding graphBinding, GCS gcs) {
        this.graphBinding = graphBinding;
        this.gcs = gcs;
    }

    public GraphBinding getGraphBinding() {
        return graphBinding;
    }

    public GCS getGcs() {
        return gcs;
    }

    @Override public String toString() {
        return "Geotag{" +
                "graphBinding=" + graphBinding +
                ", gcs=" + gcs +
                '}';
    }
}
