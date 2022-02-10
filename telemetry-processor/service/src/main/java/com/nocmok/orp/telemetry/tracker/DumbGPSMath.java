package com.nocmok.orp.telemetry.tracker;

import com.nocmok.orp.core_api.GCS;

public class DumbGPSMath {

    public double abs2(GCS a) {
        return a.lat() * a.lat() + a.lon() * a.lon();
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
}
