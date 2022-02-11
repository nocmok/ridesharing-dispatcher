package com.nocmok.orp.orp_solver.storage.notification;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import java.util.HashMap;

public class ServiceRequestOutboxStorage {

    private NamedParameterJdbcTemplate jdbcTemplate;

    @Autowired
    public ServiceRequestOutboxStorage(NamedParameterJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void insertOne(ServiceRequestOutboxEntry serviceRequestOutboxEntry) {
        var params = new HashMap<String, Object>();
        params.put("vehicleId", Long.parseLong(serviceRequestOutboxEntry.getVehicleId()));
        params.put("requestId", Long.parseLong(serviceRequestOutboxEntry.getRequestId()));
        params.put("reservationId", Long.parseLong(serviceRequestOutboxEntry.getReservationId()));
        jdbcTemplate.update(" insert into service_request_outbox(session_id, request_id, reservation_id, sent_at) " +
                " values(:vehicleId, :requestId, :reservationId, null) ", params);
    }
}
