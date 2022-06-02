package com.nocmok.orp.solver.ls;

import com.nocmok.orp.solver.api.Schedule;
import com.nocmok.orp.solver.api.ScheduleEntry;

import java.util.ArrayList;
import java.util.List;

public class LazyScheduleGenerator {

    private final List<ScheduleEntry> schedule;
    private final ScheduleEntry startNode;
    private final ScheduleEntry endNode;

    public LazyScheduleGenerator(Schedule schedule, ScheduleEntry startNode, ScheduleEntry endNode) {
        this.schedule = schedule.asList();
        this.startNode = startNode;
        this.endNode = endNode;
    }

    public List<List<ScheduleEntry>> getAllSchedules() {
        var allSchedules = new ArrayList<List<ScheduleEntry>>();
        for (int startNodePosition = 0; startNodePosition <= schedule.size(); ++startNodePosition) {
            for (int endNodePosition = startNodePosition + 1; endNodePosition <= schedule.size() + 1; ++endNodePosition) {
                var newSchedule = new ArrayList<>(schedule);
                newSchedule.add(startNodePosition, startNode);
                newSchedule.add(endNodePosition, endNode);
                allSchedules.add(newSchedule);
            }
        }
        return allSchedules;
    }
}
