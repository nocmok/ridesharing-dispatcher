package com.nocmok.orp.vss.pg;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.nocmok.orp.core_api.ScheduleNode;
import com.nocmok.orp.core_api.ScheduleNodeKind;

import java.io.IOException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class ScheduleJsonMapper {

    private final ObjectMapper objectMapper;

    public ScheduleJsonMapper() {
        this.objectMapper = JsonMapper.builder()
                .build();
    }

    private ScheduleNode mapDtoToScheduleNode(ScheduleNodeJsonDto dto) {
        return new ScheduleNode(dto.deadline, dto.load, dto.nodeId, dto.lat, dto.lon, dto.kind, dto.orderId);
    }

    private ScheduleNodeJsonDto mapScheduleNodeToDto(ScheduleNode schedule) {
        return new ScheduleNodeJsonDto(schedule.getDeadline(), schedule.getLoad(), schedule.getNodeId(), schedule.getLat(), schedule.getLon(),
                schedule.getKind(), schedule.getOrderId());
    }

    public List<ScheduleNode> decodeSchedule(String json) {
        return _decodeSchedule(json).stream()
                .map(this::mapDtoToScheduleNode)
                .collect(Collectors.toCollection(ArrayList::new));
    }

    public String encodeSchedule(List<ScheduleNode> schedule) {
        return _encodeSchedule(schedule.stream()
                .map(this::mapScheduleNodeToDto)
                .collect(Collectors.toList()));
    }

    private List<ScheduleNodeJsonDto> _decodeSchedule(String json) {
        try {
            ScheduleNodeJsonDto[] scheduleArray = objectMapper.readValue(json, ScheduleNodeJsonDto[].class);
            return new ArrayList<>(Arrays.asList(scheduleArray));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    private String _encodeSchedule(List<ScheduleNodeJsonDto> schedule) {
        try {
            return objectMapper.writeValueAsString(schedule);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
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

    private static class ScheduleNodeJsonDto {
        @JsonProperty("deadline")
        @JsonSerialize(using = InstantSerializer.class)
        @JsonDeserialize(using = InstantDeserializer.class)
        private Instant deadline;

        @JsonProperty("load")
        private int load;

        @JsonProperty("nodeId")
        private int nodeId;

        @JsonProperty("lat")
        private double lat;

        @JsonProperty("lon")
        private double lon;

        @JsonProperty("kind")
        private ScheduleNodeKind kind;

        @JsonProperty("orderId")
        private String orderId;

        public ScheduleNodeJsonDto() {

        }

        public ScheduleNodeJsonDto(Instant deadline, int load, int nodeId, double lat, double lon, ScheduleNodeKind kind, String orderId) {
            this.deadline = deadline;
            this.load = load;
            this.nodeId = nodeId;
            this.lat = lat;
            this.lon = lon;
            this.kind = kind;
            this.orderId = orderId;
        }

    }
}
