package com.nocmok.orp.solver.ls;

import com.nocmok.orp.solver.api.ScheduleNode;

import java.util.ArrayList;
import java.util.List;

public class LazyScheduleGenerator {

    private final List<ScheduleNode> schedule;
    private final ScheduleNode startNode;
    private final ScheduleNode endNode;

    public LazyScheduleGenerator(List<ScheduleNode> schedule, ScheduleNode startNode, ScheduleNode endNode) {
        this.schedule = schedule;
        this.startNode = startNode;
        this.endNode = endNode;
    }

    public List<List<ScheduleNode>> getAllSchedules() {
        var allSchedules = new ArrayList<List<ScheduleNode>>();
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
