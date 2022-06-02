package com.nocmok.orp.orp_solver.job;

import com.nocmok.orp.postgres.storage.SessionReservationStorage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class ExpiredReservationInvalidationJob {

    private SessionReservationStorage sessionReservationStorage;

    @Value("${orp.orp_solver.job.ExpiredReservationInvalidationJob.reservationTTLSeconds:120}")
    private Integer reservationTTLSeconds;

    @Autowired
    public ExpiredReservationInvalidationJob(SessionReservationStorage vehicleReservationStorage) {
        this.sessionReservationStorage = vehicleReservationStorage;
    }

    @Scheduled(fixedDelayString = "${orp.orp_solver.job.ExpiredReservationInvalidationJob.invalidationIntervalSeconds:5000}")
    public void invalidateExpiredReservations() {
        sessionReservationStorage.invalidateExpiredReservations(reservationTTLSeconds);
    }
}
