package com.nocmok.orp.orp_solver.service.dispatching.mapper;

import com.nocmok.orp.orp_solver.service.dispatching.dto.VehicleReservation;
import com.nocmok.orp.postgres.storage.dto.SessionReservationEntry;
import org.springframework.stereotype.Component;

@Component
public class SessionReservationMapper {

    public SessionReservationEntry mapReservationToStorageEntry(VehicleReservation reservation) {
        return SessionReservationEntry.builder()
                .reservationId(reservation.getReservationId())
                .vehicleId(reservation.getVehicleId())
                .requestId(reservation.getRequestId())
                .createdAt(reservation.getCreatedAt())
                .expiredAt(reservation.getExpiredAt())
                .build();
    }

    public VehicleReservation mapVehicleReservationEntryToVehicleReservation(SessionReservationEntry entry) {
        return VehicleReservation.builder()
                .reservationId(entry.getReservationId())
                .vehicleId(entry.getVehicleId())
                .createdAt(entry.getCreatedAt())
                .expiredAt(entry.getExpiredAt())
                .requestId(entry.getRequestId())
                .build();
    }
}
