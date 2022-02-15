package com.nocmok.orp.state_keeper.pg;

import com.nocmok.orp.core_api.GCS;
import com.nocmok.orp.core_api.GraphBinding;

public class Geotag {

    private GraphBinding graphBinding;

    public Geotag(GraphBinding graphBinding) {
        this.graphBinding = graphBinding;
    }

    public GraphBinding getGraphBinding() {
        return graphBinding;
    }

    @Override public String toString() {
        return "Geotag{" +
                "graphBinding=" + graphBinding +
                '}';
    }
}
