package com.nocmok.orp.orp_solver.job;

import com.nocmok.orp.orp_solver.storage.dispatching.VehicleReservationStorage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class ExpiredReservationInvalidationJob {

    private VehicleReservationStorage vehicleReservationStorage;
    private Integer reservationTTLSeconds = 60;

    @Autowired
    public ExpiredReservationInvalidationJob(VehicleReservationStorage vehicleReservationStorage) {
        this.vehicleReservationStorage = vehicleReservationStorage;
    }

    @Scheduled(fixedDelay = 10000)
    public void invalidateExpiredReservations() {
        vehicleReservationStorage.invalidateExpiredReservations(reservationTTLSeconds);
    }
}
