package com.nocmok.orp.orp_solver.kafka.orp_input.mapper;

import com.nocmok.orp.core_api.Request;
import com.nocmok.orp.kafka.orp_input.ServiceRequestMessage;

public class MatchVehiclesMessageMapper {

    public Request mapMessageToRequest(ServiceRequestMessage message) {
        return new Request(
                message.getRequestId(),
                message.getPickupNodeId(),
                message.getPickupLat(),
                message.getPickupLon(),
                message.getDropoffNodeId(),
                message.getDropoffLat(),
                message.getDropoffLon(),
                message.getRequestedAt(),
                message.getDetourConstraint(),
                message.getMaxPickupDelaySeconds(),
                message.getLoad()
        );
    }
}
