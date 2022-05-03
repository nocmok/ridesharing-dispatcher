package com.nocmok.orp.solver.kt;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.nocmok.orp.solver.api.Schedule;
import com.nocmok.orp.solver.api.ScheduleEntry;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.stream.Collectors;

@JsonSerialize(using = TreeScheduleSerializer.class)
@JsonDeserialize(using = TreeScheduleDeserializer.class)
//@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS)
public class TreeSchedule implements Schedule {

    private List<List<ScheduleEntry>> tree;
    private List<ScheduleEntry> bestSchedule;

    /**
     * @param tree - "дерево" валидных перестановок заданное в виде списка всех перестановок в дереве
     */
    public TreeSchedule(List<ScheduleEntry> bestSchedule, List<List<ScheduleEntry>> tree) {
        this.bestSchedule = new ArrayList<>(bestSchedule);
        this.tree = tree.stream()
                .map(ArrayList::new)
                .collect(Collectors.toList());
    }

    public TreeSchedule(List<ScheduleEntry> bestSchedule) {
        this.bestSchedule = bestSchedule;
        this.tree = List.of(bestSchedule);
    }

    public TreeSchedule() {

    }

    @Override public List<ScheduleEntry> asList() {
        return bestSchedule;
    }

    @Override public void removeFirstEntry() {
        if (empty()) {
            throw new NoSuchElementException("cannot remove from empty schedule");
        }
        this.tree = this.tree.stream()
                .filter(schedule -> Objects.equals(bestSchedule.get(0), schedule.get(0)))
                .peek(schedule -> schedule.remove(0))
                .collect(Collectors.toList());
        this.bestSchedule.remove(0);
    }

    @Override public boolean empty() {
        return bestSchedule == null || bestSchedule.isEmpty();
    }

    public List<List<ScheduleEntry>> asTree() {
        return this.tree;
    }
}
