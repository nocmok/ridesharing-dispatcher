package com.nocmok.orp.postgres.storage;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nocmok.orp.postgres.storage.dto.OrpOutputOutboxRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

@Repository
public class OrpOutputOutboxStorage {

    private ObjectMapper objectMapper;
    private NamedParameterJdbcTemplate jdbcTemplate;

    @Autowired
    public OrpOutputOutboxStorage(ObjectMapper objectMapper, NamedParameterJdbcTemplate jdbcTemplate) {
        this.objectMapper = objectMapper;
        this.jdbcTemplate = jdbcTemplate;
    }

    private String objectToJson(Object object) {
        try {
            return objectMapper.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    private <T> T jsonToObject(String json, Class<T> clazz) {
        try {
            return objectMapper.readValue(json, clazz);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Параметры created_at и sent_at игнорируются
     */
    public <T> void insertOneRecord(OrpOutputOutboxRecord<T> record) {
        var params = new HashMap<String, Object>();
        params.put("partition_key", record.getPartitionKey());
        params.put("payload", objectToJson(record.getPayload()));
        params.put("message_kind", record.getMessageKind());
        params.put("created_at", Timestamp.from(Instant.now()));
        jdbcTemplate.update(
                " insert into orp_output_outbox (message_id,partition_key,payload,message_kind,created_at,sent_at) " +
                        " values(nextval('orp_output_outbox_seq'),:partition_key,:payload,:message_kind,:created_at,null) ",
                params
        );
    }

    private OrpOutputOutboxRecord<String> mapResultSetToOrpOutputOutboxEntry(ResultSet rs, int nRow) throws SQLException {
        return OrpOutputOutboxRecord.<String>builder()
                .messageId(rs.getLong("message_id"))
                .partitionKey(rs.getString("partition_key"))
                .messageKind(rs.getString("message_kind"))
                .payload(rs.getString("payload"))
                .createdAt(Optional.ofNullable(rs.getTimestamp("created_at")).map(Timestamp::toInstant).orElse(null))
                .sentAt(Optional.ofNullable(rs.getTimestamp("sent_at")).map(Timestamp::toInstant).orElse(null))
                .build();
    }

    /**
     * Сообщения отдаются в отсортированном по времени создания порядке
     */
    public List<OrpOutputOutboxRecord<String>> getUnsentRecordsBatchForUpdateSkipLocked(Integer maxBatchSize) {
        var params = new HashMap<String, Object>();
        params.put("maxBatchSize", maxBatchSize);
        return jdbcTemplate.query(" select " +
                        " message_id, " +
                        " partition_key, " +
                        " message_kind, " +
                        " payload, " +
                        " created_at, " +
                        " sent_at " +
                        " from orp_output_outbox " +
                        " where sent_at is null" +
                        " order by created_at asc " +
                        " limit :maxBatchSize" +
                        " for update skip locked ", params,
                this::mapResultSetToOrpOutputOutboxEntry
        );
    }

    public <T> void updateRecordsBatch(List<OrpOutputOutboxRecord<T>> recordsBatch) {
        var batchArray = new ArrayList<>(recordsBatch);
        jdbcTemplate.getJdbcTemplate().batchUpdate(
                " update orp_output_outbox " +
                        " set " +
                        " partition_key = ?, " +
                        " message_kind = ?, " +
                        " payload = ?, " +
                        " created_at = ?, " +
                        " sent_at = ? " +
                        " where message_id = ?",
                new BatchPreparedStatementSetter() {
                    @Override public void setValues(PreparedStatement ps, int i) throws SQLException {
                        var record = batchArray.get(i);
                        ps.setString(1, record.getPartitionKey());
                        ps.setString(2, record.getMessageKind());
                        ps.setString(3, objectToJson(record.getPayload()));
                        ps.setTimestamp(4, Optional.ofNullable(record.getCreatedAt()).map(Timestamp::from).orElse(null));
                        ps.setTimestamp(5, Optional.ofNullable(record.getSentAt()).map(Timestamp::from).orElse(null));
                        ps.setLong(6, record.getMessageId());
                    }

                    @Override public int getBatchSize() {
                        return batchArray.size();
                    }
                }
        );
    }

}
