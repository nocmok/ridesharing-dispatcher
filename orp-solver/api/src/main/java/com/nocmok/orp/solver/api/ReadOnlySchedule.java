package com.nocmok.orp.solver.api;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import java.util.List;

/**
 * Класс, который используется для десериализации планов тс,
 * если нельзя десериализовать план в специализированный класс
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonTypeInfo(use = JsonTypeInfo.Id.NONE)
public class ReadOnlySchedule implements Schedule {

    @JsonProperty("schedule")
    private List<ScheduleEntry> schedule;

    public ReadOnlySchedule() {

    }

    public ReadOnlySchedule(List<ScheduleEntry> schedule) {
        this.schedule = schedule;
    }

    @Override public List<ScheduleEntry> asList() {
        return schedule;
    }

    @Override public void removeFirstEntry() {
        throw new UnsupportedOperationException("read only schedule");
    }

    @Override public boolean empty() {
        return schedule == null || schedule.isEmpty();
    }
}
