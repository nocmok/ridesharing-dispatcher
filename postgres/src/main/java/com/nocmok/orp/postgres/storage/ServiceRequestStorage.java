package com.nocmok.orp.postgres.storage;

import com.nocmok.orp.postgres.storage.dto.OrderStatus;
import com.nocmok.orp.postgres.storage.dto.ServiceRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
public class ServiceRequestStorage {

    private NamedParameterJdbcTemplate jdbcTemplate;
    private ServiceRequestIdSequence serviceRequestIdSequence;

    @Autowired
    public ServiceRequestStorage(NamedParameterJdbcTemplate jdbcTemplate, ServiceRequestIdSequence serviceRequestIdSequence) {
        this.jdbcTemplate = jdbcTemplate;
        this.serviceRequestIdSequence = serviceRequestIdSequence;
    }

    public String getIdForRequest() {
        return Long.toString(serviceRequestIdSequence.nextValue());
    }

    private ServiceRequest mapResultSetToServiceRequest(ResultSet rs, int nRow) throws SQLException {
        return new ServiceRequest(
                Long.toString(rs.getLong("request_id")),
                rs.getDouble("recorded_origin_latitude"),
                rs.getDouble("recorded_origin_longitude"),
                rs.getDouble("recorded_destination_latitude"),
                rs.getDouble("recorded_destination_longitude"),
                rs.getString("pickup_road_segment_start_node_id"),
                rs.getString("pickup_road_segment_end_node_id"),
                rs.getString("dropoff_road_segment_start_node_id"),
                rs.getString("dropoff_road_segment_end_node_id"),
                Optional.ofNullable(rs.getTimestamp("requested_at")).map(Timestamp::toInstant)
                        .orElseThrow(() -> new NullPointerException("requested_at not expected to be null")),
                rs.getDouble("detour_constraint"),
                rs.getInt("max_pickup_delay_seconds"),
                rs.getInt("load"),
                OrderStatus.valueOf(rs.getString("status")),
                Long.toString(rs.getLong("serving_session_id"))
        );
    }

    public Optional<ServiceRequest> getRequestById(String id) {
        var params = new HashMap<String, Object>();
        params.put("requestId", Long.parseLong(id));
        var requests = jdbcTemplate.query(
                " select " +
                        " request_id," +
                        " recorded_origin_latitude," +
                        " recorded_origin_longitude," +
                        " recorded_destination_latitude," +
                        " recorded_destination_longitude," +
                        " pickup_road_segment_start_node_id," +
                        " pickup_road_segment_end_node_id," +
                        " dropoff_road_segment_start_node_id," +
                        " dropoff_road_segment_end_node_id," +
                        " detour_constraint," +
                        " max_pickup_delay_seconds," +
                        " requested_at," +
                        " load," +
                        " status," +
                        " serving_session_id " +
                        " from service_request " +
                        " where request_id = :requestId",
                params, this::mapResultSetToServiceRequest);
        return requests.stream().findFirst();
    }

    public Optional<ServiceRequest> getRequestByIdForUpdate(String id) {
        var params = new HashMap<String, Object>();
        params.put("requestId", Long.parseLong(id));
        var requests = jdbcTemplate.query(
                " select " +
                        " request_id," +
                        " recorded_origin_latitude," +
                        " recorded_origin_longitude," +
                        " recorded_destination_latitude," +
                        " recorded_destination_longitude," +
                        " pickup_road_segment_start_node_id," +
                        " pickup_road_segment_end_node_id," +
                        " dropoff_road_segment_start_node_id," +
                        " dropoff_road_segment_end_node_id," +
                        " detour_constraint," +
                        " max_pickup_delay_seconds," +
                        " requested_at," +
                        " load," +
                        " status," +
                        " serving_session_id " +
                        " from service_request " +
                        " where request_id = :requestId " +
                        " for update ",
                params, this::mapResultSetToServiceRequest);
        return requests.stream().findFirst();
    }

    @Transactional
    public void updateRequestStatus(String requestId, OrderStatus updatedStatus) {
        updateOrderStatus(Long.parseLong(requestId), updatedStatus);
        insertStatusLogEntry(Long.parseLong(requestId), new ServiceRequest.OrderStatusLogEntry(updatedStatus, Instant.now()));
    }

    private void updateOrderStatus(Long requestId, OrderStatus updatedStatus) {
        var params = new HashMap<String, Object>();
        params.put("status", updatedStatus.name());
        params.put("requestId", requestId);
        jdbcTemplate.update(
                " update service_request " +
                        " set status = cast(:status as service_request_status) " +
                        " where request_id = :requestId ", params);
    }

    private void insertStatusLogEntry(Long orderId, ServiceRequest.OrderStatusLogEntry orderStatusLogEntry) {
        var params = new HashMap<String, Object>();
        params.put("orderId", orderId);
        params.put("status", orderStatusLogEntry.getOrderStatus().name());
        params.put("updatedAt", Timestamp.from(orderStatusLogEntry.getUpdatedAt()));
        jdbcTemplate.update(
                " insert into order_status_log (order_id, status, updated_at) values(:orderId, cast(:status as service_request_status), :updatedAt)", params);
    }

    public void updateServingSessionId(String requestId, String sessionId) {
        var params = new HashMap<String, Object>();
        params.put("servingSessionId", Long.parseLong(sessionId));
        params.put("requestId", Long.parseLong(requestId));
        jdbcTemplate.update(
                " update service_request " +
                        " set serving_session_id = :servingSessionId " +
                        " where request_id = :requestId ", params);
    }

    @Transactional
    public ServiceRequest storeRequest(ServiceRequest serviceRequest) {
        serviceRequest.setRequestId(getIdForRequest());
        serviceRequest.setStatus(Objects.requireNonNullElse(serviceRequest.getStatus(), OrderStatus.SERVICE_PENDING));

        insertRequest(serviceRequest);
        insertStatusLogEntry(Long.parseLong(serviceRequest.getRequestId()), new ServiceRequest.OrderStatusLogEntry(OrderStatus.SERVICE_PENDING, Instant.now()));

        return serviceRequest;
    }

    private ServiceRequest insertRequest(ServiceRequest serviceRequest) {
        var params = new HashMap<String, Object>();
        params.put("request_id", Long.parseLong(serviceRequest.getRequestId()));
        params.put("recorded_origin_latitude", serviceRequest.getRecordedOriginLatitude());
        params.put("recorded_origin_longitude", serviceRequest.getRecordedOriginLongitude());
        params.put("recorded_destination_latitude", serviceRequest.getRecordedDestinationLatitude());
        params.put("recorded_destination_longitude", serviceRequest.getRecordedDestinationLongitude());
        params.put("pickup_road_segment_start_node_id", serviceRequest.getPickupRoadSegmentStartNodeId());
        params.put("pickup_road_segment_end_node_id", serviceRequest.getPickupRoadSegmentEndNodeId());
        params.put("dropoff_road_segment_start_node_id", serviceRequest.getDropOffRoadSegmentStartNodeId());
        params.put("dropoff_road_segment_end_node_id", serviceRequest.getDropOffRoadSegmentEndNodeId());
        params.put("detour_constraint", serviceRequest.getDetourConstraint());
        params.put("max_pickup_delay_seconds", serviceRequest.getMaxPickupDelaySeconds());
        params.put("requested_at", Optional.ofNullable(serviceRequest.getRequestedAt()).map(Timestamp::from)
                .orElseThrow(() -> new NullPointerException("requested_at not expected to be null")));
        params.put("load", serviceRequest.getLoad());
        params.put("status", serviceRequest.getStatus().name());
        params.put("serving_session_id", serviceRequest.getServingSessionId() == null ? null : Long.parseLong(serviceRequest.getServingSessionId()));
        jdbcTemplate.update(" insert into service_request " +
                        " ( " +
                        " request_id," +
                        " recorded_origin_latitude," +
                        " recorded_origin_longitude," +
                        " recorded_destination_latitude," +
                        " recorded_destination_longitude," +
                        " pickup_road_segment_start_node_id," +
                        " pickup_road_segment_end_node_id," +
                        " dropoff_road_segment_start_node_id," +
                        " dropoff_road_segment_end_node_id," +
                        " detour_constraint," +
                        " max_pickup_delay_seconds," +
                        " requested_at," +
                        " load," +
                        " status," +
                        " serving_session_id " +
                        " ) " +
                        " values " +
                        " ( " +
                        "   :request_id, " +
                        "   :recorded_origin_latitude, " +
                        "   :recorded_origin_longitude, " +
                        "   :recorded_destination_latitude, " +
                        "   :recorded_destination_longitude, " +
                        "   :pickup_road_segment_start_node_id, " +
                        "   :pickup_road_segment_end_node_id, " +
                        "   :dropoff_road_segment_start_node_id, " +
                        "   :dropoff_road_segment_end_node_id, " +
                        "   :detour_constraint, " +
                        "   :max_pickup_delay_seconds, " +
                        "   :requested_at, " +
                        "   :load," +
                        "   cast(:status as service_request_status)," +
                        "   :serving_session_id " +
                        " ) ",
                params);
        return serviceRequest;
    }

    public List<String> getActiveRequestsIds() {
        return jdbcTemplate.getJdbcTemplate().queryForList("select request_id from service_request where status in ('PENDING', 'SERVING')", Long.class)
                .stream()
                .map(Object::toString)
                .collect(Collectors.toList());
    }

    public List<ServiceRequest.OrderStatusLogEntry> getOrderStatusLog(Long orderId, int page, int entriesPerPage, boolean ascending) {
        var params = new HashMap<String, Object>();
        params.put("orderId", orderId);
        params.put("fromInclusive", page * entriesPerPage);
        params.put("toExclusive", page * entriesPerPage + entriesPerPage);
        var orderStatusLog = jdbcTemplate.query(
                " select order_id, status, updated_at " +
                        " from (" +
                        "    select order_id, status, updated_at, " +
                        "    ((row_number() over (partition by order_id order by updated_at " + (ascending ? "asc" : "desc") + ")) - 1) as rn " +
                        "    from order_status_log " +
                        "    where order_id = :orderId " +
                        " ) as t " +
                        " where rn >= :fromInclusive and rn < :toExclusive ",
                params,
                (rs, rn) -> new ServiceRequest.OrderStatusLogEntry(
                        OrderStatus.valueOf(rs.getString("status")),
                        rs.getTimestamp("updated_at").toInstant()
                ));
        return orderStatusLog;
    }

    public Map<Long, List<ServiceRequest.OrderStatusLogEntry>> getOrdersStatusLogs(List<Long> orderIds, int pageNumber, int entriesPerPage,
                                                                                   boolean ascendingOrder) {
        if (orderIds.isEmpty()) {
            return Collections.emptyMap();
        }
        var params = new HashMap<String, Object>();
        params.put("ids", orderIds);
        params.put("fromInclusive", pageNumber * entriesPerPage);
        params.put("toExclusive", pageNumber * entriesPerPage + entriesPerPage);
        var statusLogEntries = jdbcTemplate.query(
                " select order_id, status, updated_at " +
                        " from (" +
                        "    select order_id, status, updated_at, " +
                        "    ((row_number() over (partition by order_id order by updated_at " + (ascendingOrder ? "asc" : "desc") + ")) - 1) as rn " +
                        "    from order_status_log " +
                        "    where order_id in (:ids) " +
                        " ) as t " +
                        " where rn >= :fromInclusive and rn < :toExclusive ",
                params,
                (rs, rn) -> Map.entry(rs.getLong("order_id"), new ServiceRequest.OrderStatusLogEntry(
                        OrderStatus.valueOf(rs.getString("status")),
                        rs.getTimestamp("updated_at").toInstant())));

        return statusLogEntries.stream()
                .collect(Collectors.groupingBy(Map.Entry::getKey, Collectors.mapping(Map.Entry::getValue, Collectors.toList())));
    }

    public Map<Long, List<ServiceRequest.OrderStatusLogEntry>> getOrdersStatusLogs(List<Long> orderIds) {
        return getOrdersStatusLogs(orderIds, 0, Integer.MAX_VALUE, true);
    }

    /**
     * Возвращает идентификаторы всех заказов, которые находятся в состоянии ACCEPTED, PICKUP_PENDING, SERVING в заданном интервале
     */
    public List<Long> getSessionActiveOrdersInsideTimeInterval(Long servingSessionId, Instant fromInclusive, Instant toInclusive) {
        var params = new HashMap<String, Object>();
        params.put("sessionId", servingSessionId);
        params.put("fromInclusive", Timestamp.from(fromInclusive));
        params.put("toInclusive", Timestamp.from(toInclusive));
        return jdbcTemplate.query("select t1.order_id\n" +
                " from " +
                " ( " +
                "     select order_id " +
                "     from order_status_log " +
                "     where order_id in (select order_id from order_assignment where session_id = :sessionId) " +
                "         and status in ('ACCEPTED', 'PICKUP_PENDING', 'SERVING') " +
                "     group by order_id " +
                "     having min(updated_at) < cast(:toInclusive as timestamp with time zone) " +
                " ) as t1 " +
                " join " +
                " ( " +
                "     select order_id " +
                "     from order_status_log " +
                "     where order_id in (select order_id from order_assignment where session_id = :sessionId) " +
                "         and status in ('SERVED', 'CANCELLED', 'SERVICE_DENIED') " +
                "     group by order_id " +
                "     having min(updated_at) > cast(:fromInclusive as timestamp with time zone) " +
                " ) as t2 " +
                " on t1.order_id = t2.order_id ", params, (rs, rn) -> rs.getLong("order_id"));
    }
}
