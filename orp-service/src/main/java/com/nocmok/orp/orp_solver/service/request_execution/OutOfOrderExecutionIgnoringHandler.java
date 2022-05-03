package com.nocmok.orp.orp_solver.service.request_execution;

import com.nocmok.orp.solver.api.ScheduleEntry;
import com.nocmok.orp.state_keeper.api.VehicleState;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class OutOfOrderExecutionIgnoringHandler implements OutOfOrderExecutionHandler {

    @Override public VehicleState handleOutOfOrderExecution(VehicleState session, ScheduleEntry entryToRemove) {
        throw new UnsupportedOperationException("out of order execution handler not implemented");

//        var skippedScheduleEntries = session.getSchedule().asList().subList(0, session.getSchedule().asList().indexOf(entryToRemove));
//
//        var skippedNodesIds = skippedScheduleEntries.stream().map(ScheduleEntry::getNodeId).collect(Collectors.toSet());
//        skippedNodesIds.remove(entryToRemove.getNodeId());
//
//        // Проверяем нет ли вершин/ребер которые были пропущены.
//        // Перемешивание контрольных точек в пределах одного ребра допускается без перепланировки.
//        if (!skippedNodesIds.isEmpty()) {
//            // TODO заменить на перепланировку (rescheduling)
//            throw new IllegalRequestExecution(
//                    "Invalid schedule execution. Some of schedule checkpoints was illegally skipped. Handling such cases is subject for further development");
//        }
//
//        if (ScheduleEntryKind.PICKUP == entryToRemove.getKind()) {
//            // Если контрольные точки одного запроса были перепутаны местами
//            if (skippedScheduleEntries.stream().anyMatch(scheduleEntry -> Objects.equals(entryToRemove.getOrderId(), scheduleEntry.getOrderId()))) {
//                throw new IllegalRequestExecution(
//                        "Invalid execution order of request with id " + entryToRemove.getOrderId() + ". PICKUP point cannot be completed after DROPOFF");
//            }
//        } else {
//            log.warn("request with id " + entryToRemove.getOrderId() + " was completed, but was not started");
//        }
//
//        session.getSchedule().remove(entryToRemove);
//
//        return session;
    }
}
