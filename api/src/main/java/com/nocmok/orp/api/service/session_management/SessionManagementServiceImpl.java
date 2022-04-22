package com.nocmok.orp.api.service.session_management;

import com.nocmok.orp.api.service.session_management.dto.SessionDto;
import com.nocmok.orp.api.service.session_management.dto.SessionInfo;
import com.nocmok.orp.graph.api.ObjectUpdater;
import com.nocmok.orp.graph.api.SpatialGraphObjectsStorage;
import com.nocmok.orp.state_keeper.api.StateKeeper;
import com.nocmok.orp.state_keeper.api.VehicleStatus;
import com.nocmok.orp.state_keeper.pg.VehicleDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class SessionManagementServiceImpl implements SessionManagementService {

    private SpatialGraphObjectsStorage graphObjectsStorage;
    private StateKeeper<?> stateKeeper;

    @Autowired
    public SessionManagementServiceImpl(SpatialGraphObjectsStorage graphObjectsStorage, StateKeeper<?> stateKeeper) {
        this.graphObjectsStorage = graphObjectsStorage;
        this.stateKeeper = stateKeeper;
    }

    @Override public SessionDto createSession(SessionDto sessionDto) {
        // TODO делать транзакционно

        var vehicle = stateKeeper.createVehicle(VehicleDto.builder()
                .capacity(sessionDto.getInitialCapacity())
                .residualCapacity(sessionDto.getInitialCapacity())
                .status(VehicleStatus.PENDING)
                .build());

        sessionDto.setSessionId(vehicle.getId());

        graphObjectsStorage.updateObject(new ObjectUpdater(
                sessionDto.getSessionId(),
                sessionDto.getSourceId(),
                sessionDto.getTargetId(),
                sessionDto.getInitialLatitude(),
                sessionDto.getInitialLongitude()
        ));

        return sessionDto;
    }

    @Override public void stopSession(String sessionId) {
        throw new UnsupportedOperationException("not implemented");
    }

    @Override public SessionInfo getSessionInfo(String sessionId) {
        throw new UnsupportedOperationException("not implemented");
    }

    // TODO добавить completedAt, createdAt
    @Override public List<SessionInfo> getActiveSessions() {
        var activeVehicles = stateKeeper.getActiveVehicles();
        return activeVehicles.stream().map(session -> SessionInfo.builder()
                        .id(session.getId())
                        .status(session.getStatus())
                        .capacity(session.getCapacity())
                        .residualCapacity(session.getResidualCapacity())
                        .schedule(session.getSchedule())
                        .build())
                .collect(Collectors.toList());
    }

    @Override public List<String> getActiveSessionsIds() {
        return stateKeeper.getActiveVehiclesIds();
    }
}
