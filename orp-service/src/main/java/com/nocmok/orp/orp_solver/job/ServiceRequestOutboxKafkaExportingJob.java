package com.nocmok.orp.orp_solver.job;

import com.nocmok.orp.orp_solver.storage.notification.ServiceRequestOutboxEntry;
import com.nocmok.orp.orp_solver.storage.notification.ServiceRequestOutboxStorage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.util.CollectionUtils;

import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Забирает батч записей из таблицы service_request_outbox,
 * преобразует их в сообщения и отправляет в кафку в топик orp.output
 */
@Slf4j
public class ServiceRequestOutboxKafkaExportingJob {

    private TransactionTemplate transactionTemplate;
    private ServiceRequestOutboxStorage serviceRequestOutboxStorage;
    private Integer maxBatchSize;
    private KafkaTemplate<String, Object> kafkaTemplate;

    public ServiceRequestOutboxKafkaExportingJob(TransactionTemplate transactionTemplate,
                                                 ServiceRequestOutboxStorage serviceRequestOutboxStorage, Integer maxBatchSize,
                                                 KafkaTemplate<String, Object> kafkaTemplate) {
        this.transactionTemplate = transactionTemplate;
        this.serviceRequestOutboxStorage = serviceRequestOutboxStorage;
        this.maxBatchSize = maxBatchSize;
        this.kafkaTemplate = kafkaTemplate;
    }

    @Scheduled(fixedDelay = 5000)
    public void exportOutboxEntriesToKafkaBatch() {
        transactionTemplate.setIsolationLevel(TransactionDefinition.ISOLATION_READ_COMMITTED);
        var batchSent = transactionTemplate.execute(transactionStatus -> {
            var batch = serviceRequestOutboxStorage.getUnsentEntriesBatchForUpdateSkipLocked(maxBatchSize);
            if (batch.isEmpty()) {
                return Collections.emptyList();
            }
            var nowTime = Instant.now();
            batch.forEach(entry -> entry.setSentAt(nowTime));
            serviceRequestOutboxStorage.updateEntriesBatch(batch);
            sendNotificationsBatch(batch.stream()
                    .map(this::mapServiceRequestOutboxEntryToNotification)
                    .collect(Collectors.toList()));
            return batch;
        });
        if (!CollectionUtils.isEmpty(batchSent)) {
            log.info("sent " + batchSent.size() + " notifications");
        } else {
            log.info("sent 0 notifications");
        }
    }

    private ServiceRequestNotificationMessage mapServiceRequestOutboxEntryToNotification(ServiceRequestOutboxEntry serviceRequestOutboxEntry) {
        return ServiceRequestNotificationMessage.builder()
                .requestId(serviceRequestOutboxEntry.getRequestId())
                .vehicleId(serviceRequestOutboxEntry.getVehicleId())
                .reservationId(serviceRequestOutboxEntry.getReservationId())
                .build();
    }

    private void sendNotificationsBatch(List<ServiceRequestNotificationMessage> notificationBatch) {
        for (var message : notificationBatch) {
            kafkaTemplate.send("orp.output", message.getVehicleId(), message);
        }
    }
}
