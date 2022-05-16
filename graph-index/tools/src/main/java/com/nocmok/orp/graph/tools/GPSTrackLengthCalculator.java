package com.nocmok.orp.graph.tools;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public final class GPSTrackLengthCalculator {

    private double outlineVelocityThreshold;
    private double zeroVelocityThreshold;

    public GPSTrackLengthCalculator(double outlineVelocityThreshold, double zeroVelocityThreshold) {
        this.outlineVelocityThreshold = outlineVelocityThreshold;
        this.zeroVelocityThreshold = zeroVelocityThreshold;
    }

    public Double getGPSTrackLength(List<GPSTrackEntry> track) {
        if (track.isEmpty()) {
            return 0d;
        }

        track = removeOutlines(track);
        track = removeZeroVelocityDrift(track);

        double distance = 0d;
        var source = track.get(0);
        for (var target : track) {
            distance += EarthMath.spheroidalDistanceDegrees(source.latitude, source.longitude, target.latitude, target.longitude);
            source = target;
        }
        return distance;
    }

    private double velocityBetweenGPSes(GPSTrackEntry source, GPSTrackEntry target) {
        long timeFromSourceToTargetMillis = target.getRecordedAt().toEpochMilli() - source.getRecordedAt().toEpochMilli();
        double distance = EarthMath.spheroidalDistanceDegrees(source.getLatitude(), source.getLongitude(), target.getLatitude(), target.getLongitude());
        if (timeFromSourceToTargetMillis == 0) {
            // если скорость неизвестна, то будем возвращать 0
            return 0;
        }
        return distance * 1000 / timeFromSourceToTargetMillis;
    }

    private List<GPSTrackEntry> removeOutlines(List<GPSTrackEntry> track) {
        if (track == null || track.size() < 2) {
            return track;
        }
        var it = track.iterator();
        var filteredPoints = new ArrayList<GPSTrackEntry>();
        filteredPoints.add(it.next());
        while (it.hasNext()) {
            var nextPoint = it.next();
            if (velocityBetweenGPSes(filteredPoints.get(filteredPoints.size() - 1), nextPoint) > outlineVelocityThreshold) {
                filteredPoints.remove(filteredPoints.size() - 1);
                if (filteredPoints.isEmpty()) {
                    if (it.hasNext()) {
                        filteredPoints.add(it.next());
                    }
                }
            } else {
                filteredPoints.add(nextPoint);
            }
        }
        return filteredPoints;
    }

    private List<GPSTrackEntry> removeZeroVelocityDrift(List<GPSTrackEntry> track) {
        if (track == null || track.size() < 2) {
            return track;
        }
        var filteredPoints = new ArrayList<GPSTrackEntry>();
        var it = track.iterator();
        var prevPoint = it.next();
        while (it.hasNext()) {
            var nextPoint = it.next();
            if (velocityBetweenGPSes(prevPoint, nextPoint) >= zeroVelocityThreshold) {
                filteredPoints.add(prevPoint);
            }
            prevPoint = nextPoint;
        }
        filteredPoints.add(prevPoint);
        return filteredPoints;
    }

    public static final class GPSTrackEntry {
        private Double latitude;
        private Double longitude;
        private Instant recordedAt;
        private Double accuracy;

        public GPSTrackEntry(Double latitude, Double longitude, Instant recordedAt, Double accuracy) {
            this.latitude = latitude;
            this.longitude = longitude;
            this.recordedAt = recordedAt;
            this.accuracy = accuracy;
        }

        public Double getLatitude() {
            return latitude;
        }

        public Double getLongitude() {
            return longitude;
        }

        public Instant getRecordedAt() {
            return recordedAt;
        }

        public Double getAccuracy() {
            return accuracy;
        }

        @Override public String toString() {
            return "GPSTrackEntry{" +
                    "latitude=" + latitude +
                    ", longitude=" + longitude +
                    ", recordedAt=" + recordedAt +
                    ", accuracy=" + accuracy +
                    '}';
        }
    }
}
