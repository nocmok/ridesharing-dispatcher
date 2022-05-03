package com.nocmok.orp.solver.kt;

import com.nocmok.orp.solver.api.ScheduleEntry;
import com.nocmok.orp.solver.api.ScheduleEntryKind;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;

public class KineticTreeTest {

    @Test
    public void testTreeInitialization() {
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

        var schedulePermutations = List.of(
                List.of(bestSchedule.get(0), bestSchedule.get(1), bestSchedule.get(2), bestSchedule.get(3)),
                List.of(bestSchedule.get(0), bestSchedule.get(2), bestSchedule.get(1), bestSchedule.get(3)),
                List.of(bestSchedule.get(0), bestSchedule.get(2), bestSchedule.get(3), bestSchedule.get(1)),
                List.of(bestSchedule.get(2), bestSchedule.get(0), bestSchedule.get(1), bestSchedule.get(3)),
                List.of(bestSchedule.get(2), bestSchedule.get(0), bestSchedule.get(3), bestSchedule.get(1)),
                List.of(bestSchedule.get(2), bestSchedule.get(3), bestSchedule.get(0), bestSchedule.get(1))
        );

        var kineticTree = new KineticTree<ScheduleEntry, ScheduleKTNode>(ScheduleKTNode::new, new KineticTree.Validator<ScheduleEntry, ScheduleKTNode>() {
            @Override public boolean validate(ScheduleKTNode parent, ScheduleKTNode child) {
                return false;
            }

            @Override public boolean validate(ScheduleKTNode tree) {
                return false;
            }
        }, new KineticTree.Aggregator<ScheduleEntry, ScheduleKTNode>() {
            @Override public void aggregate(ScheduleKTNode parent, ScheduleKTNode child) {

            }

            @Override public void aggregate(ScheduleKTNode tree) {

            }
        }, schedulePermutations);

        var allPermutations = kineticTree.allPermutations();
        allPermutations.size();
    }
}
