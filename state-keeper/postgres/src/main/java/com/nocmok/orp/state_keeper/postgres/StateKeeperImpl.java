package com.nocmok.orp.state_keeper.postgres;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nocmok.orp.postgres.storage.SessionStorage;
import com.nocmok.orp.postgres.storage.dto.Session;
import com.nocmok.orp.postgres.storage.dto.SessionStatus;
import com.nocmok.orp.solver.api.EmptySchedule;
import com.nocmok.orp.solver.api.ReadOnlySchedule;
import com.nocmok.orp.solver.api.Schedule;
import com.nocmok.orp.state_keeper.api.DefaultVehicle;
import com.nocmok.orp.state_keeper.api.StateKeeper;
import com.nocmok.orp.state_keeper.api.VehicleState;
import com.nocmok.orp.state_keeper.api.VehicleStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class StateKeeperImpl implements StateKeeper<DefaultVehicle> {

    private final SessionStorage sessionStorage;
    private final ObjectMapper objectMapper;

    @Autowired
    public StateKeeperImpl(SessionStorage sessionStorage, ObjectMapper objectMapper) {
        this.sessionStorage = sessionStorage;
        this.objectMapper = objectMapper;
    }

    public Schedule tryParseDefaultSchedule(String json) throws Exception {
        return objectMapper.readValue(json, ReadOnlySchedule.class);
    }

    private Schedule jsonToSchedule(String json) {
        try {
            return objectMapper.readValue(json, Schedule.class);
        } catch (Exception e) {
            try {
                return tryParseDefaultSchedule(json);
            } catch (Exception suppress) {
                var exceptionToThrow = new RuntimeException(e);
                exceptionToThrow.addSuppressed(suppress);
                throw exceptionToThrow;
            }
        }
    }

    private String scheduleToJson(Schedule schedule) {
        try {
            return objectMapper.writeValueAsString(schedule);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private DefaultVehicle mapSessionToVehicleState(Session session) {
        return DefaultVehicle.builder()
                .id(session.getSessionId() == null ? null : Objects.toString(session.getSessionId()))
                .status(null)
                .residualCapacity(session.getResidualCapacity() == null ? null : session.getResidualCapacity().intValue())
                .capacity(session.getTotalCapacity() == null ? null : session.getTotalCapacity().intValue())
                .schedule(session.getScheduleJson() == null ? null : jsonToSchedule(session.getScheduleJson()))
                .build();
    }

    private VehicleStatus mapSessionStatusToVehicleStatus(SessionStatus sessionStatus) {
        switch (sessionStatus) {
            case PENDING:
                return VehicleStatus.PENDING;
            case SERVING:
                return VehicleStatus.SERVING;
            case CLOSED:
            case FROZEN:
                throw new UnsupportedOperationException("CLOSED, FROZEN session statuses not expected here");
            default:
                throw new IllegalArgumentException("unknown session status " + sessionStatus);
        }
    }

    private Session mapVehicleStateToSession(VehicleState vehicleDto) {
        return Session.builder()
                .sessionId(vehicleDto.getId() == null ? null : Long.parseLong(vehicleDto.getId()))
                .totalCapacity(vehicleDto.getCapacity() == null ? null : vehicleDto.getCapacity().longValue())
                .residualCapacity(vehicleDto.getResidualCapacity() == null ? null : vehicleDto.getResidualCapacity().longValue())
                .scheduleJson(vehicleDto.getSchedule() == null ? null : scheduleToJson(vehicleDto.getSchedule()))
                .build();
    }

    private SessionStatus mapVehicleStatusToSessionStatus(VehicleStatus vehicleStatus) {
        switch (vehicleStatus) {
            case PENDING:
                return SessionStatus.PENDING;
            case SERVING:
                return SessionStatus.SERVING;
            default:
                throw new IllegalArgumentException("unknown vehicle status " + vehicleStatus);
        }
    }

    @Override public List<String> getActiveVehiclesIds() {
        return sessionStorage.getActiveSessionsIds().stream()
                .map(Objects::toString)
                .collect(Collectors.toList());
    }

    @Transactional
    @Override public List<DefaultVehicle> getVehiclesByIds(List<String> ids) {
        var longIds = ids.stream().map(Long::parseLong).collect(Collectors.toList());
        var sessions = sessionStorage.getActiveSessionsByIds(longIds);
        var sessionStatuses = sessionStorage.getSessionStatuses(longIds);
        return sessions.stream()
                .map(this::mapSessionToVehicleState)
                .peek(vehicle -> vehicle.setStatus(mapSessionStatusToVehicleStatus(sessionStatuses.get(Long.parseLong(vehicle.getId())))))
                .collect(Collectors.toList());
    }

    @Transactional
    @Override public void updateVehicle(VehicleState vehicle) {
        sessionStorage.updateSession(mapVehicleStateToSession(vehicle));
        sessionStorage.updateSessionStatus(Long.parseLong(vehicle.getId()), mapVehicleStatusToSessionStatus(vehicle.getStatus()));
    }

    @Override public VehicleState createVehicle(VehicleState vehicle) {
        vehicle.setSchedule(Objects.requireNonNullElseGet(vehicle.getSchedule(), EmptySchedule::new));
        vehicle.setStatus(Objects.requireNonNullElse(vehicle.getStatus(), VehicleStatus.PENDING));

        var createdSession = sessionStorage.createSession(mapVehicleStateToSession(vehicle), mapVehicleStatusToSessionStatus(vehicle.getStatus()));
        vehicle.setId(Long.toString(createdSession.getSessionId()));

        return vehicle;
    }

    @Override public List<DefaultVehicle> getActiveVehiclesByIdsForUpdate(List<String> ids) {
        var longIds = ids.stream().map(Long::parseLong).collect(Collectors.toList());
        var sessions = sessionStorage.getActiveSessionsByIdsForUpdate(longIds);
        var sessionStatuses = sessionStorage.getSessionStatuses(longIds);
        return sessions.stream()
                .map(this::mapSessionToVehicleState)
                .peek(vehicle -> vehicle.setStatus(mapSessionStatusToVehicleStatus(sessionStatuses.get(Long.parseLong(vehicle.getId())))))
                .collect(Collectors.toList());
    }
}
