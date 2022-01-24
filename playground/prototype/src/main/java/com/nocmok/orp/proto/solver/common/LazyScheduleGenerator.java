package com.nocmok.orp.proto.solver.common;

import com.nocmok.orp.proto.solver.ScheduleCheckpoint;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

public class LazyScheduleGenerator {

    private List<ScheduleCheckpoint> templateSchedule;
    private ScheduleCheckpoint pickup;
    private ScheduleCheckpoint dropoff;

    public LazyScheduleGenerator(List<ScheduleCheckpoint> templateSchedule, ScheduleCheckpoint pickup, ScheduleCheckpoint dropoff) {
        this.templateSchedule = templateSchedule;
        this.pickup = pickup;
        this.dropoff = dropoff;
    }

    public void forEachSchedule(Consumer<List<ScheduleCheckpoint>> callback) {
        forEachSchedule(new ArrayList<>(), callback);
    }

    private void forEachSchedule(ArrayList<ScheduleCheckpoint> schedule, Consumer<List<ScheduleCheckpoint>> callback) {
        for (int pickupPosition = 0; pickupPosition <= templateSchedule.size(); ++pickupPosition) {
            for (int dropoffPosition = pickupPosition + 1; dropoffPosition <= templateSchedule.size() + 1; ++dropoffPosition) {
                schedule.clear();
                schedule.addAll(templateSchedule);

                schedule.add(pickupPosition, pickup);
                schedule.add(dropoffPosition, dropoff);

                callback.accept(Collections.unmodifiableList(schedule));
            }
        }
    }
}
