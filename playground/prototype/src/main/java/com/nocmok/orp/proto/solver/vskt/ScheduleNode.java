package com.nocmok.orp.proto.solver.vskt;

import com.nocmok.orp.proto.solver.ScheduleCheckpoint;
import com.nocmok.orp.proto.solver.common.KineticTree;
import lombok.Getter;
import lombok.Setter;

public class ScheduleNode extends KineticTree.TreeNode<ScheduleCheckpoint, ScheduleNode> {

    @Getter
    @Setter
    private int bestTime;

    // Остаточная вместимость тс перед заходом в вершину
    @Getter
    @Setter
    private int capacity;

    public ScheduleNode() {
        super();
    }

    public ScheduleNode(ScheduleCheckpoint value) {
        super(value);
    }

    @Override public ScheduleNode copy() {
        var copy = new ScheduleNode(this.value);
        copy.bestTime = this.bestTime;
        copy.capacity = this.capacity;
        return copy;
    }
}
