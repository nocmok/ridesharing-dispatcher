package com.nocmok.orp.proto.solver.vskt;

import com.nocmok.orp.proto.solver.common.KineticTree;
import com.nocmok.orp.proto.solver.ScheduleCheckpoint;
import lombok.Getter;
import lombok.Setter;

public class ScheduleNode extends KineticTree.TreeNode<ScheduleCheckpoint, ScheduleNode> {

    @Getter
    @Setter
    private int bestTime;

    public ScheduleNode() {
        super();
    }

    public ScheduleNode(ScheduleCheckpoint value) {
        super(value);
    }

    @Override public ScheduleNode copy() {
        var copy = new ScheduleNode(this.value);
        copy.bestTime = this.bestTime;
        return copy;
    }
}
