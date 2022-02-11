package com.nocmok.orp.orp_solver.service.dispatching;

import com.nocmok.orp.orp_solver.storage.dispatching.ReservationTicketSequence;
import com.nocmok.orp.orp_solver.storage.dispatching.VehicleReservationEntry;
import com.nocmok.orp.orp_solver.storage.dispatching.VehicleReservationStorage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 *
 */
public class VehicleReservationService {

    private TransactionTemplate transactionTemplate;
    private ReservationTicketSequence reservationTicketSequence;
    private VehicleReservationStorage vehicleReservationStorage;

    public VehicleReservationService(TransactionTemplate transactionTemplate,
                                     ReservationTicketSequence reservationTicketSequence) {
        this.transactionTemplate = transactionTemplate;
        this.reservationTicketSequence = reservationTicketSequence;
    }

    private VehicleReservationEntry mapReservationToStorageEntry(VehicleReservation reservation) {
        return VehicleReservationEntry.builder()
                .reservationId(reservation.getReservationId())
                .vehicleId(reservation.getVehicleId())
                .requestId(reservation.getRequestId())
                .createdAt(Instant.now())
                .expiredAt(null)
                .build();
    }

    public List<VehicleReservation> tryReserveVehicles(ReservationCallback callback) {
        return transactionTemplate.execute(status -> {
            var idsToCheckReservation = callback.getVehicleIdsToCheckReservation();
            var reservedIds =
                    vehicleReservationStorage.getNotExpiredReservationsByVehicleIdsForUpdate(idsToCheckReservation).stream()
                            .map(VehicleReservationEntry::getVehicleId)
                            .collect(Collectors.toList());
            var feasibleIds = new ArrayList<>(idsToCheckReservation);
            feasibleIds.removeAll(reservedIds);


            var reservations = callback.reserveVehicles(feasibleIds);
            reservations.forEach(reservation -> reservation.setReservationId(reservationTicketSequence.nextValue()));

            vehicleReservationStorage.insertVehicleReservationBatch(reservations.stream()
                    .map(this::mapReservationToStorageEntry)
                    .collect(Collectors.toList()));

            callback.handleReservations(reservations);

            return reservations;
        });
    }

    public interface ReservationCallback {

        /**
         * Возвращает список идентификаторов тс, для которых нужно проверить резервирование
         */
        List<String> getVehicleIdsToCheckReservation();

        /**
         * Принимает список доступных для резервирования тс.
         * Возвращает список тс которых нужно зарезервировать в виде специального объекта-тикета
         */
        List<VehicleReservation> reserveVehicles(List<String> feasibleVehicleIds);

        /**
         * Принимает тикеты возвращенные из reserveVehicles(), обогащенные идентификаторами резервации
         */
        void handleReservations(List<VehicleReservation> tickets);
    }
}
