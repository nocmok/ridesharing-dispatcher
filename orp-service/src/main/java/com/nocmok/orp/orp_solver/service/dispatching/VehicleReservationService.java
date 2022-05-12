package com.nocmok.orp.orp_solver.service.dispatching;

import com.nocmok.orp.orp_solver.service.dispatching.dto.VehicleReservation;
import com.nocmok.orp.orp_solver.service.dispatching.mapper.SessionReservationMapper;
import com.nocmok.orp.postgres.storage.SessionReservationSequence;
import com.nocmok.orp.postgres.storage.SessionReservationStorage;
import com.nocmok.orp.postgres.storage.dto.SessionReservationEntry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 *
 */
@Service
public class VehicleReservationService {

    private TransactionTemplate transactionTemplate;
    private SessionReservationSequence sessionReservationSequence;
    private SessionReservationStorage sessionReservationStorage;
    private SessionReservationMapper sessionReservationMapper;

    @Autowired
    public VehicleReservationService(TransactionTemplate transactionTemplate,
                                     SessionReservationSequence reservationTicketSequence,
                                     SessionReservationStorage vehicleReservationStorage,
                                     SessionReservationMapper vehicleReservationMapper) {
        this.transactionTemplate = transactionTemplate;
        this.sessionReservationSequence = reservationTicketSequence;
        this.sessionReservationStorage = vehicleReservationStorage;
        this.sessionReservationMapper = vehicleReservationMapper;
    }

    public List<VehicleReservation> tryReserveVehicles(ReservationCallback callback) {
        return transactionTemplate.execute(status -> {
            var idsToCheckReservation = callback.getVehicleIdsToCheckReservation();
            var reservedIds =
                    sessionReservationStorage.getNotExpiredReservationsByVehicleIdsForUpdate(idsToCheckReservation).stream()
                            .map(SessionReservationEntry::getVehicleId)
                            .collect(Collectors.toList());
            var feasibleIds = new ArrayList<>(idsToCheckReservation);
            feasibleIds.removeAll(reservedIds);


            var reservations = callback.reserveVehicles(feasibleIds);
            reservations.forEach(reservation -> {
                reservation.setReservationId(sessionReservationSequence.nextValue());
                //
                reservation.setCreatedAt(Instant.now());
                reservation.setExpiredAt(null);
            });

            sessionReservationStorage.insertVehicleReservationBatch(reservations.stream()
                    .map(sessionReservationMapper::mapReservationToStorageEntry)
                    .collect(Collectors.toList()));

            callback.handleReservations(reservations);

            return reservations;
        });
    }

    public Optional<VehicleReservation> getReservationById(String id) {
        return sessionReservationStorage.getReservationById(id)
                .map(sessionReservationMapper::mapVehicleReservationEntryToVehicleReservation);
    }

    public void updateReservation(VehicleReservation reservation) {
        sessionReservationStorage.updateReservation(sessionReservationMapper.mapReservationToStorageEntry(reservation));
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
