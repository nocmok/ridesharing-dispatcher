package com.nocmok.orp.proto.solver.common;

import com.nocmok.orp.proto.solver.ScheduleCheckpoint;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

public class AllSchedulesGenerator {

    private List<ScheduleCheckpoint> schedule;
    private PermutationGenerator permutationGenerator;

    public AllSchedulesGenerator(List<ScheduleCheckpoint> schedule, ScheduleCheckpoint pickup, ScheduleCheckpoint dropoff) {
        this.schedule = new ArrayList<>(schedule);
        this.schedule.add(pickup);
        this.schedule.add(dropoff);
        this.permutationGenerator = new PermutationGenerator(this.schedule.size());
    }

    private List<ScheduleCheckpoint> sample(List<Integer> positions, List<ScheduleCheckpoint> permutation) {
        permutation.clear();
        for (int i = 0; i < schedule.size(); ++i) {
            permutation.add(schedule.get(positions.get(i)));
        }
        return permutation;
    }

    public void forEachSchedule(Consumer<List<ScheduleCheckpoint>> callback) {
        var schedule = new ArrayList<ScheduleCheckpoint>();
        permutationGenerator.forEachPermutation((positions) ->
                callback.accept(Collections.unmodifiableList(sample(positions, schedule)))
        );
    }
}
