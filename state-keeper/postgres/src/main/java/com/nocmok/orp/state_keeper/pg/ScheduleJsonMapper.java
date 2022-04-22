package com.nocmok.orp.state_keeper.pg;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.nocmok.orp.state_keeper.api.ScheduleEntry;
import com.nocmok.orp.state_keeper.api.ScheduleEntryKind;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Component
class ScheduleJsonMapper {

    private final ObjectMapper objectMapper;

    public ScheduleJsonMapper(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    private ScheduleEntry mapDtoToScheduleEntry(ScheduleEntryJsonDto dto) {
        return new ScheduleEntry(dto.deadline, dto.load, dto.nodeId, dto.latitude, dto.longitude, dto.kind, dto.orderId);
    }

    private ScheduleEntryJsonDto mapScheduleEntryToDto(ScheduleEntry schedule) {
        return new ScheduleEntryJsonDto(
                schedule.getDeadline(), schedule.getLoad(),
                schedule.getNodeId(), schedule.getLatitude(),
                schedule.getLongitude(), schedule.getKind(),
                schedule.getOrderId());
    }

    public List<ScheduleEntry> decodeSchedule(String json) {
        return _decodeSchedule(json).stream()
                .map(this::mapDtoToScheduleEntry)
                .collect(Collectors.toCollection(ArrayList::new));
    }

    public String encodeSchedule(List<ScheduleEntry> schedule) {
        return _encodeSchedule(schedule.stream()
                .map(this::mapScheduleEntryToDto)
                .collect(Collectors.toList()));
    }

    private List<ScheduleEntryJsonDto> _decodeSchedule(String json) {
        try {
            ScheduleEntryJsonDto[] scheduleArray = objectMapper.readValue(json, ScheduleEntryJsonDto[].class);
            return new ArrayList<>(Arrays.asList(scheduleArray));
        } catch (Exception e) {
            return Collections.emptyList();
        }
    }

    private String _encodeSchedule(List<ScheduleEntryJsonDto> schedule) {
        try {
            return objectMapper.writeValueAsString(schedule);
        } catch (Exception e) {
            return "[]";
        }
    }

    private static class InstantSerializer extends JsonSerializer<Instant> {
        @Override public void serialize(Instant value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
            gen.writeString(Objects.toString(value));
        }
    }

    private static class InstantDeserializer extends JsonDeserializer<Instant> {
        @Override public Instant deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JacksonException {
            return Instant.parse(p.getText());
        }
    }

    private static class ScheduleEntryJsonDto {
        @JsonProperty("deadline")
        @JsonSerialize(using = InstantSerializer.class)
        @JsonDeserialize(using = InstantDeserializer.class)
        private Instant deadline;

        @JsonProperty("load")
        private Integer load;

        @JsonProperty("nodeId")
        private String nodeId;

        @JsonProperty("latitude")
        private Double latitude;

        @JsonProperty("longitude")
        private Double longitude;

        @JsonProperty("kind")
        private ScheduleEntryKind kind;

        @JsonProperty("orderId")
        private String orderId;

        public ScheduleEntryJsonDto() {

        }

        public ScheduleEntryJsonDto(Instant deadline, Integer load, String nodeId, Double latitude, Double longitude,
                                    ScheduleEntryKind kind, String orderId) {
            this.deadline = deadline;
            this.load = load;
            this.nodeId = nodeId;
            this.latitude = latitude;
            this.longitude = longitude;
            this.kind = kind;
            this.orderId = orderId;
        }
    }
}
