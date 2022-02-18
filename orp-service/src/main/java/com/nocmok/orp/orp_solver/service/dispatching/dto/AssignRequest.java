package com.nocmok.orp.orp_solver.service.dispatching.dto;

import lombok.Builder;

@Builder
public class AssignRequest {

    private String reservationId;
    private String vehicleId;
    private String serviceRequestId;

    public AssignRequest(String reservationId, String vehicleId, String serviceRequestId) {
        this.reservationId = reservationId;
        this.vehicleId = vehicleId;
        this.serviceRequestId = serviceRequestId;
    }

    public String getReservationId() {
        return reservationId;
    }

    public String getVehicleId() {
        return vehicleId;
    }

    public String getServiceRequestId() {
        return serviceRequestId;
    }

    @Override public String toString() {
        return "AssignRequest{" +
                "reservationId='" + reservationId + '\'' +
                ", vehicleId='" + vehicleId + '\'' +
                ", serviceRequestId='" + serviceRequestId + '\'' +
                '}';
    }
}
