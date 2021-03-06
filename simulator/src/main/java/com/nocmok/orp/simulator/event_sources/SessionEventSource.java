package com.nocmok.orp.simulator.event_sources;

import com.nocmok.orp.postgres.storage.SessionStorage;
import com.nocmok.orp.simulator.event_bus.EventBus;
import com.nocmok.orp.simulator.event_bus.event.NewSessionEvent;
import com.nocmok.orp.simulator.storage.VehicleSessionStorage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
@Slf4j
public class SessionEventSource {

    private EventBus eventBus;
    private VehicleSessionStorage vehicleSessionStorage;

    // Время создания самой новой вычитанной сессии
    private Instant latestKnownSessionCreatedAt = Instant.ofEpochMilli(0);

    private SessionStorage sessionStorage;

    @Autowired
    public SessionEventSource(EventBus eventBus, VehicleSessionStorage vehicleSessionStorage) {
        this.eventBus = eventBus;
        this.vehicleSessionStorage = vehicleSessionStorage;
    }

    @Scheduled(fixedRate = 1000)
    public void updateActiveSessionList() {


        // В первый раз вычитывает всю таблицу vehicle_session запоминает самое большое время в createdAt
        var newSessions = vehicleSessionStorage.readActiveVehiclesCreatedAfterTimestampOrderedByCreationTime(latestKnownSessionCreatedAt);
        if (newSessions.isEmpty()) {
            return;
        }

        var latestSession = newSessions.get(newSessions.size() - 1);
        latestKnownSessionCreatedAt = latestSession.getCreatedAt();

        newSessions.forEach(session -> eventBus.emit(NewSessionEvent.builder()
                .sessionId(session.getSessionId())
                .build()));
    }
}
