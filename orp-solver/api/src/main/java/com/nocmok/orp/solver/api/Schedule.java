package com.nocmok.orp.solver.api;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import java.util.List;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.CLASS,
        defaultImpl = ReadOnlySchedule.class
)
public interface Schedule {

    @JsonGetter("schedule")
    List<ScheduleEntry> asList();

    @JsonIgnore
    void removeFirstEntry();

    @JsonIgnore
    boolean empty();
}
