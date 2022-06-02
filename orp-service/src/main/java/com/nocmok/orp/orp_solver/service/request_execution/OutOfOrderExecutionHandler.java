package com.nocmok.orp.orp_solver.service.request_execution;

import com.nocmok.orp.solver.api.ScheduleEntry;
import com.nocmok.orp.state_keeper.api.VehicleState;

/**
 * Стратегия обработки ситуаций, в которых выполнение плана происходит не в запланированном порядке
 */
public interface OutOfOrderExecutionHandler {

    /**
     *
     * @param entryToRemove точка в плане для которой пришел запрос на выполнение вне плана
     *
     */
    VehicleState handleOutOfOrderExecution(VehicleState session, ScheduleEntry entryToRemove);
}
