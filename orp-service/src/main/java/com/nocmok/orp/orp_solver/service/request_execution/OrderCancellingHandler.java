package com.nocmok.orp.orp_solver.service.request_execution;

import com.nocmok.orp.postgres.storage.dto.ServiceRequest;
import com.nocmok.orp.state_keeper.api.VehicleState;

public interface OrderCancellingHandler {

    VehicleState handleOrderCancelling(VehicleState session, ServiceRequest request);
}
