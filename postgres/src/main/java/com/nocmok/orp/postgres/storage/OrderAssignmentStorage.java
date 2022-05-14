package com.nocmok.orp.postgres.storage;

import com.nocmok.orp.postgres.storage.dto.OrderAssignment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;

@Repository
public class OrderAssignmentStorage {

    private NamedParameterJdbcTemplate jdbcTemplate;

    @Autowired
    public OrderAssignmentStorage(NamedParameterJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void insertAssignment(OrderAssignment orderAssignment) {
        var params = new HashMap<String, Object>();
        params.put("orderId", orderAssignment.getOrderId());
        params.put("sessionId", orderAssignment.getSessionId());
        params.put("assignedAt", Timestamp.from(orderAssignment.getAssignedAt()));
        int rowsAffected =
                jdbcTemplate.update(" insert into order_assignment (order_id, session_id, assigned_at) values(:orderId, :sessionId, :assignedAt) ", params);
        if (rowsAffected != 1) {
            throw new RuntimeException("failed to insert order assignment");
        }
    }

    public List<OrderAssignment> getSessionAssignments(Long sessionId, int page, int entriesPerPage, boolean ascending) {
        var params = new HashMap<String, Object>();
        params.put("sessionId", sessionId);
        params.put("fromInclusive", page * entriesPerPage);
        params.put("toExclusive", page * entriesPerPage + entriesPerPage);
        return jdbcTemplate.query(" select order_id, session_id, assigned_at " +
                        " from " +
                        " ( " +
                        "   select order_id, session_id, assigned_at, ((row_number() over (partition by session_id order by assigned_at " +
                        (ascending ? "asc" : "desc") + ")) - 1) as rn " +
                        "   from order_assignment " +
                        "   where session_id = :sessionId" +
                        " ) as t where rn >= :fromInclusive and rn < :toExclusive ",
                params,
                (rs, rn) -> OrderAssignment.builder()
                        .orderId(rs.getLong("order_id"))
                        .sessionId(rs.getLong("session_id"))
                        .assignedAt(rs.getTimestamp("assigned_at").toInstant())
                        .build());
    }


}
