package com.nocmok.orp.orp_solver.service;

import com.nocmok.orp.orp_solver.service.dto.VehicleReservation;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.transaction.support.TransactionTemplate;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 *
 */
public class VehicleReservationService {

    private TransactionTemplate transactionTemplate;
    private NamedParameterJdbcTemplate jdbcTemplate;

    public VehicleReservationService(TransactionTemplate transactionTemplate, NamedParameterJdbcTemplate jdbcTemplate) {
        this.transactionTemplate = transactionTemplate;
        this.jdbcTemplate = jdbcTemplate;
    }

    /**
     * Принимает список идентификаторов тс - кандидатов на резервирование и коллбэк.
     * <p>
     * В коллбэк передастся список идентификаторов - подмножество candidateVehicleIds в котором будут содержаться еще не зарезервированные тс.
     * <p>
     * Коллбэк возвращает список резервирований, которые будут осуществлены.
     * <p>
     * Из метода возвращается тот же список резервирований, что вернулся из коллбека
     */
    public List<VehicleReservation> tryReserveVehicles(List<String> candidateVehicleIds, Function<List<String>, List<VehicleReservation>> callback) {
        return transactionTemplate.execute(status -> {
            var reservedIds = jdbcTemplate.query(
                    " select " +
                            " session_id " +
                            " from vehicle_reservation " +
                            " where expired_at is null " +
                            " for update ", (rs, nRow) -> rs.getLong("session_id"));

            var ids = new ArrayList<>(candidateVehicleIds);
            ids.removeAll(reservedIds.stream()
                    .map(Object::toString)
                    .collect(Collectors.toUnmodifiableList()));

            var reservations = callback.apply(ids);

            jdbcTemplate.getJdbcTemplate().batchUpdate(
                    " insert into vehicle_reservation values(?,?,null) ",
                    new BatchPreparedStatementSetter() {
                        @Override public void setValues(PreparedStatement ps, int i) throws SQLException {
                            var reservation = reservations.get(i);
                            ps.setLong(1, Long.parseLong(reservation.getVehicleId()));
                            ps.setLong(2, Long.parseLong(reservation.getRequestId()));
                        }

                        @Override public int getBatchSize() {
                            return reservations.size();
                        }
                    });

            return reservations;
        });
    }
}
