package com.nocmok.orp.api.service.session;

import com.nocmok.orp.api.service.session.dto.SessionStatistics;
import com.nocmok.orp.graph.tools.GPSTrackLengthCalculator;
import com.nocmok.orp.postgres.storage.SessionStorage;
import com.nocmok.orp.postgres.storage.TelemetryStorage;
import com.nocmok.orp.postgres.storage.dto.Session;
import com.nocmok.orp.postgres.storage.dto.SessionStatus;
import com.nocmok.orp.postgres.storage.dto.Telemetry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class SessionStatisticsServiceImpl implements SessionStatisticsService {

    private SessionStorage sessionStorage;
    private TelemetryStorage telemetryStorage;
    private GPSTrackLengthCalculator gpsTrackLengthCalculator;

    @Autowired
    public SessionStatisticsServiceImpl(SessionStorage sessionStorage, TelemetryStorage telemetryStorage,
                                        GPSTrackLengthCalculator gpsTrackLengthCalculator) {
        this.sessionStorage = sessionStorage;
        this.telemetryStorage = telemetryStorage;
        this.gpsTrackLengthCalculator = gpsTrackLengthCalculator;
    }

    private GPSTrackLengthCalculator.GPSTrackEntry mapTelemetryToGPSTrackEntry(Telemetry telemetry) {
        return new GPSTrackLengthCalculator.GPSTrackEntry(telemetry.getLatitude(), telemetry.getLongitude(), telemetry.getRecordedAt(),
                telemetry.getAccuracy());
    }

    @Override
    public Optional<SessionStatistics> getTotalSessionStatistics(String sessionId) {
        var telemetry = telemetryStorage.getAllSessionTelemetry(Long.parseLong(sessionId));
        return Optional.of(SessionStatistics.builder()
                .distanceTravelled(
                        gpsTrackLengthCalculator.getGPSTrackLength(telemetry.stream().map(this::mapTelemetryToGPSTrackEntry).collect(Collectors.toList())))
                .build());
    }

    private Double getTotalDistanceTravelledBySessionWithinTimeIntervals(String sessionId, List<Instant[]> timeIntervals) {
        return timeIntervals.stream()
                .map(fromTo -> telemetryStorage.getSessionTelemetryInsideInterval(Long.parseLong(sessionId), fromTo[0], fromTo[1]))
                .map(gpsTrack -> gpsTrack.stream().map(this::mapTelemetryToGPSTrackEntry).collect(Collectors.toList()))
                .map(gpsTrackLengthCalculator::getGPSTrackLength)
                .reduce(0d, Double::sum);
    }

    /**
     * Статистика сессии в разрезе статуса сессии.
     */
    @Override
    public Optional<SessionStatistics> getSessionStatisticsByStatus(String sessionId, SessionStatus status) {
        var statusLog = sessionStorage.getSessionStatusLog(Long.parseLong(sessionId), 0, Integer.MAX_VALUE, true);
        if (statusLog.isEmpty()) {
            return Optional.empty();
        }
        var timeIntervalsToConsider = new ArrayList<Instant[]>();
        Session.StatusLogEntry prevStatusLogEntry = null;
        for (var statusLogEntry : statusLog) {
            if (prevStatusLogEntry != null && prevStatusLogEntry.getStatus() == status) {
                timeIntervalsToConsider.add(new Instant[]{prevStatusLogEntry.getTimestamp(), statusLogEntry.getTimestamp()});
            }
            prevStatusLogEntry = statusLogEntry;
        }
        if (prevStatusLogEntry.getStatus() == status) {
            timeIntervalsToConsider.add(new Instant[]{prevStatusLogEntry.getTimestamp(), Instant.now()});
        }
        if (timeIntervalsToConsider.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(SessionStatistics.builder()
                .distanceTravelled(getTotalDistanceTravelledBySessionWithinTimeIntervals(sessionId, timeIntervalsToConsider))
                .build());
    }

    @Override public Double getDistanceTravelledBySessionWithinTimeInterval(String sessionId, Instant fromInclusive, Instant toInclusive) {
        return getTotalDistanceTravelledBySessionWithinTimeIntervals(sessionId, List.<Instant[]>of(new Instant[]{fromInclusive, toInclusive}));
    }
}
