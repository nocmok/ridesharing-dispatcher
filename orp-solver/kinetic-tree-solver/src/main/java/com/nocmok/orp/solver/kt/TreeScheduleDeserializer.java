package com.nocmok.orp.solver.kt;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.TreeNode;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.jsontype.TypeDeserializer;
import com.nocmok.orp.solver.api.ScheduleEntry;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class TreeScheduleDeserializer extends StdDeserializer<TreeSchedule> {

    public TreeScheduleDeserializer(Class<TreeSchedule> clazz) {
        super(clazz);
    }

    public TreeScheduleDeserializer() {
        this(null);
    }

    private List<ScheduleEntry> deserializeSchedule(TreeNode tree, ObjectMapper mapper) throws IOException, JacksonException {
        var scheduleNode = tree.get("schedule");
        if (!scheduleNode.isArray()) {
            throw new RuntimeException("invalid json format. schedule field should be an array");
        }
        ScheduleEntry[] scheduleArray = mapper.readValue(scheduleNode.traverse(), ScheduleEntry[].class);
        return Arrays.asList(scheduleArray);
    }

    private List<List<Integer>> deserializeIndexTree(TreeNode tree, ObjectMapper mapper) throws IOException, JacksonException {
        var treeNode = tree.get("tree");
        if (!treeNode.isArray()) {
            throw new RuntimeException("invalid json format. tree field should be an array");
        }
        Integer[][] indexTree = mapper.readValue(treeNode.traverse(), Integer[][].class);
        return Arrays.stream(indexTree).map(Arrays::asList).collect(Collectors.toList());
    }

    private List<ScheduleEntry> decodeSchedule(List<ScheduleEntry> templateSchedule, List<Integer> encodedSchedule) {
        return encodedSchedule.stream().map(templateSchedule::get).collect(Collectors.toList());
    }

    private List<List<ScheduleEntry>> decodeIndexTreeAsSchedulesTree(List<ScheduleEntry> templateSchedule, List<List<Integer>> indexTree) {
        return indexTree.stream()
                .map(encodedSchedule -> decodeSchedule(templateSchedule, encodedSchedule))
                .collect(Collectors.toList());
    }

    @Override public TreeSchedule deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JacksonException {
        var mapper = (ObjectMapper) p.getCodec();
        var tree = p.readValueAsTree();

        var schedule = deserializeSchedule(tree, mapper);
        var indexTree = deserializeIndexTree(tree, mapper);
        var scheduleTree = decodeIndexTreeAsSchedulesTree(schedule, indexTree);
        return new TreeSchedule(schedule, scheduleTree);
    }

    @Override public Object deserializeWithType(JsonParser p, DeserializationContext ctxt, TypeDeserializer typeDeserializer) throws IOException {
        return deserialize(p, ctxt);
    }
}
