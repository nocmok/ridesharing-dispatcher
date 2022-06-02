package com.nocmok.orp.solver.kt;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.nocmok.orp.solver.api.ScheduleEntry;
import com.nocmok.orp.solver.api.ScheduleEntryKind;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;

public class TreeScheduleSerializerTest {

    private ObjectMapper objectMapper = JsonMapper.builder().addModule(new JavaTimeModule()).build();

    @Test
    public void testSerializeSimpleSchedule() throws Exception {
        var treeSchedule = new TreeSchedule(List.of(
                ScheduleEntry.builder()
                        .deadline(Instant.ofEpochMilli(1651609889895L))
                        .kind(ScheduleEntryKind.PICKUP)
                        .latitude(56.4324)
                        .longitude(37.5353)
                        .nodeId("1234")
                        .load(1)
                        .orderId("1234")
                        .build(),
                ScheduleEntry.builder()
                        .deadline(Instant.ofEpochMilli(1651609889895L))
                        .kind(ScheduleEntryKind.DROPOFF)
                        .latitude(56.542234)
                        .longitude(37.234523)
                        .nodeId("33523")
                        .load(1)
                        .orderId("1234")
                        .build()
        ));

        String json = objectMapper.writeValueAsString(treeSchedule);
    }

    @Test
    public void testSerializeComplexSchedule() throws Exception {
        var bestSchedule = List.of(
                ScheduleEntry.builder()
                        .deadline(Instant.ofEpochMilli(1651609889895L))
                        .kind(ScheduleEntryKind.PICKUP)
                        .latitude(56.4324)
                        .longitude(37.5353)
                        .nodeId("1234")
                        .load(1)
                        .orderId("1")
                        .build(),
                ScheduleEntry.builder()
                        .deadline(Instant.ofEpochMilli(1651609889895L))
                        .kind(ScheduleEntryKind.DROPOFF)
                        .latitude(56.542234)
                        .longitude(37.234523)
                        .nodeId("33523")
                        .load(1)
                        .orderId("1")
                        .build(),
                ScheduleEntry.builder()
                        .deadline(Instant.ofEpochMilli(1651609889895L))
                        .kind(ScheduleEntryKind.PICKUP)
                        .latitude(56.543324)
                        .longitude(37.545353)
                        .nodeId("132134")
                        .load(1)
                        .orderId("2")
                        .build(),
                ScheduleEntry.builder()
                        .deadline(Instant.ofEpochMilli(1651609889895L))
                        .kind(ScheduleEntryKind.DROPOFF)
                        .latitude(56.5423434)
                        .longitude(37.22345523)
                        .nodeId("333223")
                        .load(1)
                        .orderId("2")
                        .build()
        );

        var treeSchedule = new TreeSchedule(bestSchedule, List.of(
                List.of(bestSchedule.get(0), bestSchedule.get(1), bestSchedule.get(2), bestSchedule.get(3)),
                List.of(bestSchedule.get(0), bestSchedule.get(2), bestSchedule.get(1), bestSchedule.get(3)),
                List.of(bestSchedule.get(0), bestSchedule.get(2), bestSchedule.get(3), bestSchedule.get(1)),
                List.of(bestSchedule.get(2), bestSchedule.get(0), bestSchedule.get(1), bestSchedule.get(3)),
                List.of(bestSchedule.get(2), bestSchedule.get(0), bestSchedule.get(3), bestSchedule.get(1)),
                List.of(bestSchedule.get(2), bestSchedule.get(3), bestSchedule.get(0), bestSchedule.get(1))
        ));

        String json = objectMapper.writeValueAsString(treeSchedule);
    }
}
