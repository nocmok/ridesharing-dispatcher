package com.nocmok.orp.solver.kt;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.jsontype.TypeSerializer;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import com.nocmok.orp.solver.api.ScheduleEntry;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class TreeScheduleSerializer extends StdSerializer<TreeSchedule> {

    public TreeScheduleSerializer(Class<TreeSchedule> clazz) {
        super(clazz);
    }

    public TreeScheduleSerializer() {
        this(null);
    }

    private void addScheduleProperty(TreeSchedule value, JsonGenerator gen, SerializerProvider provider) throws IOException {
        gen.writeArrayFieldStart("schedule");
        for (var scheduleEntry : value.asList()) {
            gen.writeObject(scheduleEntry);
        }
        gen.writeEndArray();
    }

    private Map<ScheduleEntry, Integer> getScheduleTreeEntriesEncoding(TreeSchedule value) {
        var encoding = new HashMap<ScheduleEntry, Integer>();
        int code = 0;
        for (var scheduleEntry : value.asList()) {
            encoding.put(scheduleEntry, code);
            ++code;
        }
        return encoding;
    }

    private List<Integer> encodeSchedule(List<ScheduleEntry> schedule, Map<ScheduleEntry, Integer> encoding) {
        return schedule.stream().map(encoding::get).collect(Collectors.toList());
    }

    private List<List<Integer>> getEncodedTree(TreeSchedule value) {
        var encoding = getScheduleTreeEntriesEncoding(value);
        return value.asTree().stream().map(schedule -> encodeSchedule(schedule, encoding)).collect(Collectors.toList());
    }

    private void addTreeProperty(TreeSchedule value, JsonGenerator gen, SerializerProvider provider) throws IOException {
        gen.writeArrayFieldStart("tree");

        var encodedTree = getEncodedTree(value);

        for (var encodedSchedule : encodedTree) {
            gen.writeArray(encodedSchedule.stream().mapToInt(i -> i).toArray(), 0, encodedSchedule.size());
        }

        gen.writeEndArray();
    }

    private void addTypeInfo(TreeSchedule value, JsonGenerator gen, SerializerProvider provider) throws IOException {
        gen.writeStringField("@class", TreeSchedule.class.getName());
    }

    @Override public void serialize(TreeSchedule value, JsonGenerator gen, SerializerProvider provider) throws IOException {
        gen.writeStartObject();

        addTypeInfo(value, gen, provider);
        addScheduleProperty(value, gen, provider);
        addTreeProperty(value, gen, provider);

        gen.writeEndObject();
    }

    @Override public void serializeWithType(TreeSchedule value, JsonGenerator gen, SerializerProvider serializers, TypeSerializer typeSer) throws IOException {
        serialize(value, gen, serializers);
    }
}
