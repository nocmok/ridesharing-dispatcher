package com.nocmok.orp.orp_solver.kafka.orp_input.mapper;

import com.nocmok.orp.kafka.orp_input.AssignRequestMessage;
import com.nocmok.orp.orp_solver.service.dispatching.dto.AssignRequest;

public class AssignRequestMessageMapper {

    public AssignRequest mapAssignRequestMessageToAssignRequest(AssignRequestMessage assignRequestMessage) {
        return AssignRequest.builder()
                .reservationId(assignRequestMessage.getReservationId())
                .vehicleId(assignRequestMessage.getVehicleId())
                .serviceRequestId(assignRequestMessage.getServiceRequestId())
                .build();
    }
}
