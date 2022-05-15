package com.nocmok.orp.graph.tools;

import java.time.Instant;
import java.util.List;

public final class GPSTrackUtils {

    public static Double getGPSTrackLength(List<GPSTrackEntry> track) {
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

    // TODO
    private static List<GPSTrackEntry> removeOutlines(List<GPSTrackEntry> track) {
        return track;
    }

    // TODO
    private static List<GPSTrackEntry> removeZeroVelocityDrift(List<GPSTrackEntry> track) {
        return track;
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
