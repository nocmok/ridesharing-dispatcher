package com.nocmok.orp.solver.ls;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.nocmok.orp.solver.api.Schedule;
import com.nocmok.orp.solver.api.ScheduleEntry;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class ListSchedule implements Schedule {

    @JsonProperty("schedule")
    private List<ScheduleEntry> schedule;

    public ListSchedule() {

    }

    public ListSchedule(List<ScheduleEntry> schedule) {
        this.schedule = new ArrayList<>(Objects.requireNonNullElse(schedule, Collections.emptyList()));
    }

    @Override public List<ScheduleEntry> asList() {
        return schedule;
    }

    @Override public void removeFirstEntry() {
        if (empty()) {
            throw new UnsupportedOperationException("cannot remove from empty schedule");
        }
        schedule.remove(0);
    }

    @Override public boolean empty() {
        return schedule == null || schedule.isEmpty();
    }
}
