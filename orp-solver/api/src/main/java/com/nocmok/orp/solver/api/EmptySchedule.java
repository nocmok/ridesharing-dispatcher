package com.nocmok.orp.solver.api;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;

@JsonIgnoreProperties(ignoreUnknown = true)
public class EmptySchedule implements Schedule {

    public EmptySchedule() {

    }

    @Override public List<ScheduleEntry> asList() {
        return Collections.emptyList();
    }

    @Override public void removeFirstEntry() {
        throw new NoSuchElementException("cannot remove from empty schedule");
    }

    @Override public boolean empty() {
        return true;
    }
}
