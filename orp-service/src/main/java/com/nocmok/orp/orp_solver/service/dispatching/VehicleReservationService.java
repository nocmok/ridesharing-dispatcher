package com.nocmok.orp.orp_solver.service.dispatching;

import com.nocmok.orp.orp_solver.storage.dispatching.ReservationTicketSequence;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.transaction.support.TransactionTemplate;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

/**
 *
 */
public class VehicleReservationService {

    private TransactionTemplate transactionTemplate;
    private NamedParameterJdbcTemplate jdbcTemplate;
    private ReservationTicketSequence reservationTicketSequence;

    public VehicleReservationService(TransactionTemplate transactionTemplate, NamedParameterJdbcTemplate jdbcTemplate,
                                     ReservationTicketSequence reservationTicketSequence) {
        this.transactionTemplate = transactionTemplate;
        this.jdbcTemplate = jdbcTemplate;
        this.reservationTicketSequence = reservationTicketSequence;
    }

    private VehicleReservationTicketDto mapReservationToTicket(VehicleReservationDto reservation) {
        return new VehicleReservationTicketDto(reservationTicketSequence.nextValue(),
                reservation.getVehicleId(),
                reservation.getRequestId());
    }

    public List<VehicleReservationTicketDto> tryReserveVehicles(ReservationCallback callback) {
        return transactionTemplate.execute(status -> {
            var idsToCheckReservation = callback.getVehicleIdsToCheckReservation();

            var params = new HashMap<String, Object>();
            params.put("ids", idsToCheckReservation.stream()
                    .map(Long::parseLong)
                    .collect(Collectors.toList()));

            var reservedIds = jdbcTemplate.query(
                    " select " +
                            " session_id " +
                            " from vehicle_reservation " +
                            " where expired_at is null and session_id in (:ids) " +
                            " for update ", params, (rs, nRow) -> rs.getLong("session_id"));

            var feasibleIds = new ArrayList<>(idsToCheckReservation);
            feasibleIds.removeAll(reservedIds.stream()
                    .map(Object::toString)
                    .collect(Collectors.toUnmodifiableList()));

            var reservationTickets = callback.reserveVehicles(feasibleIds).stream()
                    .map(this::mapReservationToTicket)
                    .collect(Collectors.toUnmodifiableList());

            jdbcTemplate.getJdbcTemplate().batchUpdate(
                    " insert into vehicle_reservation values(?,?,?,null) ",
                    new BatchPreparedStatementSetter() {
                        @Override public void setValues(PreparedStatement ps, int i) throws SQLException {
                            var ticket = reservationTickets.get(i);
                            ps.setLong(1, Long.parseLong(ticket.getReservationId()));
                            ps.setLong(2, Long.parseLong(ticket.getVehicleId()));
                            ps.setLong(3, Long.parseLong(ticket.getRequestId()));
                        }

                        @Override public int getBatchSize() {
                            return reservationTickets.size();
                        }
                    });

            callback.handleReservationTickets(reservationTickets);

            return reservationTickets;
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
        List<VehicleReservationDto> reserveVehicles(List<String> feasibleVehicleIds);

        /**
         * Принимает тикеты возвращенные из reserveVehicles(), обогащенные идентификаторами резервации
         */
        void handleReservationTickets(List<VehicleReservationTicketDto> tickets);
    }
}
