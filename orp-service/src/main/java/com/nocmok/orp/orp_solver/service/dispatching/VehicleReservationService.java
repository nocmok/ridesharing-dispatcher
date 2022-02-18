package com.nocmok.orp.orp_solver.service.dispatching;

import com.nocmok.orp.orp_solver.service.dispatching.dto.VehicleReservation;
import com.nocmok.orp.orp_solver.service.dispatching.mapper.VehicleReservationMapper;
import com.nocmok.orp.orp_solver.storage.dispatching.ReservationTicketSequence;
import com.nocmok.orp.orp_solver.storage.dispatching.VehicleReservationEntry;
import com.nocmok.orp.orp_solver.storage.dispatching.VehicleReservationStorage;
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
    private ReservationTicketSequence reservationTicketSequence;
    private VehicleReservationStorage vehicleReservationStorage;
    private VehicleReservationMapper vehicleReservationMapper;

    @Autowired
    public VehicleReservationService(TransactionTemplate transactionTemplate,
                                     ReservationTicketSequence reservationTicketSequence,
                                     VehicleReservationStorage vehicleReservationStorage,
                                     VehicleReservationMapper vehicleReservationMapper) {
        this.transactionTemplate = transactionTemplate;
        this.reservationTicketSequence = reservationTicketSequence;
        this.vehicleReservationStorage = vehicleReservationStorage;
        this.vehicleReservationMapper = vehicleReservationMapper;
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
            reservations.forEach(reservation -> {
                reservation.setReservationId(reservationTicketSequence.nextValue());
                //
                reservation.setCreatedAt(Instant.now());
                reservation.setExpiredAt(null);
            });

            vehicleReservationStorage.insertVehicleReservationBatch(reservations.stream()
                    .map(vehicleReservationMapper::mapReservationToStorageEntry)
                    .collect(Collectors.toList()));

            callback.handleReservations(reservations);

            return reservations;
        });
    }

    public Optional<VehicleReservation> getReservationById(String id) {
        return vehicleReservationStorage.getReservationById(id)
                .map(vehicleReservationMapper::mapVehicleReservationEntryToVehicleReservation);
    }

    public void updateReservation(VehicleReservation reservation) {
        vehicleReservationStorage.updateReservation(vehicleReservationMapper.mapReservationToStorageEntry(reservation));
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
