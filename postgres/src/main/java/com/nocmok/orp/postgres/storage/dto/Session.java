package com.nocmok.orp.postgres.storage.dto;

import com.nocmok.orp.postgres.storage.filter.EnumField;
import com.nocmok.orp.postgres.storage.filter.Field;
import com.nocmok.orp.postgres.storage.filter.IntegerField;
import com.nocmok.orp.postgres.storage.filter.LongField;
import com.nocmok.orp.postgres.storage.filter.StringField;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.Instant;

@Data
@AllArgsConstructor
@Builder
public class Session {
    private Long sessionId;
    private String scheduleJson;
    private Long totalCapacity;
    private Long residualCapacity;
    private SessionStatus sessionStatus;

    @Data
    @AllArgsConstructor
    public static class StatusLogEntry {
        private SessionStatus status;
        private Instant timestamp;
    }

    public static final class Fields {
        public static final Field<Long, Long> sessionId = new LongField("session_id");
        public static final Field<String, String> scheduleJson = new StringField("schedule_json");
        public static final Field<Integer, Integer> totalCapacity = new IntegerField("total_capacity");
        public static final Field<Integer, Integer> residualCapacity = new IntegerField("residual_capacity");
        public static final Field<String, SessionStatus> status = new EnumField<SessionStatus>("status", "vehicle_status");
    }
}
