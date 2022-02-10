package com.nocmok.orp.road_index.mem_stub;

import com.nocmok.orp.core_api.GCS;

class Math2d {

    public double abs(GCS a) {
        return Math.hypot(a.lon(), a.lat());
    }

    public double abs2(GCS a) {
        return a.lat() * a.lat() + a.lon() * a.lon();
    }

    public GCS div(GCS a, double val) {
        return new GCS(a.lat() / val, a.lon() / val);
    }

    public GCS mul(GCS a, double scalar) {
        return new GCS(a.lat() * scalar, a.lon() * scalar);
    }

    public GCS sum(GCS a, GCS b) {
        return new GCS(a.lat() + b.lat(), a.lon() + b.lon());
    }

    public GCS sub(GCS a, GCS b) {
        return new GCS(a.lat() - b.lat(), a.lon() - b.lon());
    }

    public double dotProduct(GCS a, GCS b) {
        return a.lat() * b.lat() + a.lon() * b.lon();
    }

    public double distance(GCS a, GCS b) {
        return Math.hypot(a.lat() - b.lat(), a.lon() - b.lon());
    }

    public GCS projection2d(GCS point, GCS startNode, GCS endNode) {
        GCS B = sub(endNode, startNode);
        GCS C = sub(point, startNode);
        return sum(startNode, mul(B, dotProduct(B, C) / abs2(B)));
    }
}
