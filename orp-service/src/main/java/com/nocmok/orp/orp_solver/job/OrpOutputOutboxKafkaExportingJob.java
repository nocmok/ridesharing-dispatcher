package com.nocmok.orp.orp_solver.job;

import com.nocmok.orp.kafka.orp_output.OrpOutputMessage;
import com.nocmok.orp.orp_solver.storage.notification.OrpOutputOutboxStorage;
import com.nocmok.orp.orp_solver.storage.notification.dto.OrpOutputOutboxRecord;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.header.internals.RecordHeader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.util.CollectionUtils;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Component
@Slf4j
public class OrpOutputOutboxKafkaExportingJob {

    private TransactionTemplate transactionTemplate;
    private OrpOutputOutboxStorage orpOutputOutboxStorage;
    private KafkaTemplate<String, Object> kafkaTemplate;

    @Value("${orp.orp_solver.job.OrpOutputOutboxKafkaExportingJob.maxBatchSize:1000}")
    private Integer maxBatchSize;

    @Autowired
    public OrpOutputOutboxKafkaExportingJob(TransactionTemplate transactionTemplate,
                                            OrpOutputOutboxStorage orpOutputOutboxStorage,
                                            KafkaTemplate<String, Object> kafkaTemplate) {
        this.transactionTemplate = transactionTemplate;
        this.orpOutputOutboxStorage = orpOutputOutboxStorage;
        this.kafkaTemplate = kafkaTemplate;
    }

    @Scheduled(fixedDelayString = "${orp.orp_solver.job.OrpOutputOutboxKafkaExportingJob.exportIntervalSeconds:5000}")
    public void exportOutboxEntriesToKafkaBatch() {
        transactionTemplate.setIsolationLevel(TransactionDefinition.ISOLATION_READ_COMMITTED);
        var batchSent = transactionTemplate.execute(transactionStatus -> {
            var batch = orpOutputOutboxStorage.getUnsentRecordsBatchForUpdateSkipLocked(maxBatchSize);
            if (batch.isEmpty()) {
                return Collections.emptyList();
            }
            var nowTime = Instant.now();
            batch.forEach(record -> record.setSentAt(nowTime));
            orpOutputOutboxStorage.updateRecordsBatch(batch);
            sendNotificationsBatch(batch.stream()
                    .map(this::mapOrpOutputOutboxRecordToOrpOutputMessage)
                    .collect(Collectors.toList()));
            return batch;
        });
        if (!CollectionUtils.isEmpty(batchSent)) {
            log.info("sent " + batchSent.size() + " notifications");
        } else {
            log.info("sent 0 notifications");
        }
    }

    private OrpOutputMessage mapOrpOutputOutboxRecordToOrpOutputMessage(OrpOutputOutboxRecord<String> orpOutputOutboxRecord) {
        return OrpOutputMessage.builder()
                .partitionKey(orpOutputOutboxRecord.getPartitionKey())
                .messageKind(orpOutputOutboxRecord.getMessageKind())
                .payload(orpOutputOutboxRecord.getPayload())
                .build();
    }

    private void sendNotificationsBatch(List<OrpOutputMessage> notificationBatch) {
        for (var message : notificationBatch) {
            kafkaTemplate.send(new ProducerRecord<>(
                    "orp.output",
                    null,
                    message.getPartitionKey(),
                    message.getPayload(),
                    List.of(new RecordHeader("__TypeId__", message.getMessageKind().getBytes(StandardCharsets.UTF_8)))
            ));
        }
    }
}
