package com.nocmok.orp.postgres.storage.dto;

import com.nocmok.orp.postgres.storage.filter.ConvertingField;
import com.nocmok.orp.postgres.storage.filter.DoubleField;
import com.nocmok.orp.postgres.storage.filter.EnumField;
import com.nocmok.orp.postgres.storage.filter.Field;
import com.nocmok.orp.postgres.storage.filter.InstantField;
import com.nocmok.orp.postgres.storage.filter.IntegerField;
import com.nocmok.orp.postgres.storage.filter.LongField;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.sql.Timestamp;
import java.time.Instant;

@Data
@Builder
@AllArgsConstructor
public class ServiceRequest {
    private String requestId;

    private Double recordedOriginLatitude;

    private Double recordedOriginLongitude;

    private Double recordedDestinationLatitude;

    private Double recordedDestinationLongitude;

    private String pickupRoadSegmentStartNodeId;

    private String pickupRoadSegmentEndNodeId;

    private String dropOffRoadSegmentStartNodeId;

    private String dropOffRoadSegmentEndNodeId;

    private Instant requestedAt;

    private Double detourConstraint;

    private Integer maxPickupDelaySeconds;

    private Integer load;

    private OrderStatus status;

    private String servingSessionId;

    @Data
    @AllArgsConstructor
    public static class OrderStatusLogEntry {
        private OrderStatus orderStatus;
        private Instant updatedAt;
    }

    public static final class Fields {
        public static final Field<Long, String> requestId = new ConvertingField<>("request_id", Long::parseLong);
        public static final Field<Double, Double> recordedOriginLatitude = new DoubleField("recorded_origin_latitude");
        public static final Field<Double, Double> recordedOriginLongitude = new DoubleField("recorded_origin_longitude");
        public static final Field<Double, Double> recordedDestinationLatitude = new DoubleField("recorded_destination_latitude");
        public static final Field<Double, Double> recordedDestinationLongitude = new DoubleField("recorded_destination_longitude");
        public static final Field<Long, Long> pickupRoadSegmentStartNodeId = new LongField("pickup_road_segment_start_node_id");
        public static final Field<Long, Long> pickupRoadSegmentEndNodeId = new LongField("pickup_road_segment_end_node_id");
        public static final Field<Long, Long> dropOffRoadSegmentStartNodeId = new LongField("dropoff_road_segment_start_node_id");
        public static final Field<Long, Long> dropOffRoadSegmentEndNodeId = new LongField("dropoff_road_segment_end_node_id");
        public static final Field<Timestamp, Instant> requestedAt = new InstantField("requested_at");
        public static final Field<Double, Double> detourConstraint = new DoubleField("detour_constraint");
        public static final Field<Integer, Integer> maxPickupDelaySeconds = new IntegerField("max_pickup_delay_seconds");
        public static final Field<Integer, Integer> load = new IntegerField("load");
        public static final Field<String, OrderStatus> status = new EnumField<>("status", "service_request_status");
        public static final Field<Long, String> servingSessionId = new ConvertingField<>("serving_session_id", Long::parseLong);
    }

}