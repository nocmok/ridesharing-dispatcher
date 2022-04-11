package com.nocmok.orp.orp_solver.service.dispatching.mapper;

import com.nocmok.orp.solver.api.ScheduleNode;
import com.nocmok.orp.solver.api.ScheduleNodeKind;
import com.nocmok.orp.state_keeper.api.ScheduleEntry;
import com.nocmok.orp.state_keeper.api.ScheduleEntryKind;
import org.springframework.stereotype.Component;

import static com.nocmok.orp.state_keeper.api.ScheduleEntryKind.DROPOFF;
import static com.nocmok.orp.state_keeper.api.ScheduleEntryKind.PICKUP;

@Component
public class VehicleStateMapper {

    public ScheduleNodeKind mapScheduleNodeKindToScheduleEntryKind(ScheduleEntryKind scheduleEntryKind) {
        switch (scheduleEntryKind) {
            case PICKUP:
                return ScheduleNodeKind.PICKUP;
            case DROPOFF:
                return ScheduleNodeKind.DROPOFF;
            default:
                throw new IllegalArgumentException("unknown schedule entry kind " + scheduleEntryKind);
        }
    }

    public ScheduleNode mapScheduleNodeToScheduleEntry(ScheduleEntry scheduleEntry) {
        return new ScheduleNode(
                scheduleEntry.getDeadline(),
                scheduleEntry.getLoad(),
                scheduleEntry.getNodeId(),
                scheduleEntry.getLatitude(),
                scheduleEntry.getLongitude(),
                mapScheduleNodeKindToScheduleEntryKind(scheduleEntry.getKind()),
                scheduleEntry.getOrderId()
        );
    }

    public ScheduleEntryKind mapScheduleNodeKindToScheduleEntryKind(ScheduleNodeKind scheduleEntryKind) {
        switch (scheduleEntryKind) {
            case PICKUP:
                return PICKUP;
            case DROPOFF:
                return DROPOFF;
            default:
                throw new IllegalArgumentException("unknown schedule entry kind " + scheduleEntryKind);
        }
    }

    public ScheduleEntry mapScheduleNodeToScheduleEntry(ScheduleNode scheduleNode) {
        return new ScheduleEntry(
                scheduleNode.getDeadline(),
                scheduleNode.getLoad(),
                scheduleNode.getNodeId(),
                scheduleNode.getLatitude(),
                scheduleNode.getLongitude(),
                mapScheduleNodeKindToScheduleEntryKind(scheduleNode.getKind()),
                scheduleNode.getOrderId()
        );
    }
}
