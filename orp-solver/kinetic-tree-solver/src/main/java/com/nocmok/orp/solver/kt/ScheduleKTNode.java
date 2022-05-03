package com.nocmok.orp.solver.kt;

import com.nocmok.orp.solver.api.ScheduleEntry;

import java.time.Instant;

/**
 * Класс обертка для использования контрольных точек плана в качестве вершин кинетического дерева
 */
class ScheduleKTNode extends KineticTree.TreeNode<ScheduleEntry, ScheduleKTNode> {

    /**
     * Лучшее время прибытия в эту контрольную точку
     */
    private Instant bestEntryTime;

    /**
     * Остаточная вместимость тс перед заходом в эту контрольную точку
     */
    private Integer residualCapacityBeforeEntry;

    public ScheduleKTNode(ScheduleEntry value) {
        super(value);
    }

    public ScheduleKTNode() {

    }

    public void bestEntryTime(Instant bestTime) {
        this.bestEntryTime = bestTime;
    }

    public Instant bestEntryTime() {
        return this.bestEntryTime;
    }

    public void residualCapacityBeforeEntry(Integer capacity) {
        this.residualCapacityBeforeEntry = capacity;
    }

    public Integer residualCapacityBeforeEntry() {
        return this.residualCapacityBeforeEntry;
    }

    @Override public ScheduleKTNode copy() {
        var copy = new ScheduleKTNode(this.value);
        copy.bestEntryTime = this.bestEntryTime;
        copy.residualCapacityBeforeEntry = this.residualCapacityBeforeEntry;
        return copy;
    }
}
