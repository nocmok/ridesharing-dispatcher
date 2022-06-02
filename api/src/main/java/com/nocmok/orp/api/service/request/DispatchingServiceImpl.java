package com.nocmok.orp.api.service.request;

import com.nocmok.orp.kafka.orp_input.ServiceRequestMessage;
import com.nocmok.orp.postgres.storage.ServiceRequestStorage;
import com.nocmok.orp.postgres.storage.dto.ServiceRequest;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.header.internals.RecordHeader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.List;

@Service
public class DispatchingServiceImpl implements DispatchingService {

    private ServiceRequestStorage requestInfoStorage;
    private KafkaTemplate<String, Object> kafkaTemplate;

    @Autowired
    public DispatchingServiceImpl(ServiceRequestStorage requestInfoStorage,
                                  KafkaTemplate<String, Object> kafkaTemplate) {
        this.requestInfoStorage = requestInfoStorage;
        this.kafkaTemplate = kafkaTemplate;
    }

    private ServiceRequestMessage mapRequestInfoToOrpInputMessage(ServiceRequest requestInfo) {
        return ServiceRequestMessage.builder()
                .requestId(requestInfo.getRequestId())
                .recordedOriginLatitude(requestInfo.getRecordedOriginLatitude())
                .recordedOriginLongitude(requestInfo.getRecordedOriginLongitude())
                .recordedDestinationLatitude(requestInfo.getRecordedDestinationLatitude())
                .recordedDestinationLongitude(requestInfo.getRecordedDestinationLongitude())
                .pickupRoadSegmentStartNodeId(requestInfo.getPickupRoadSegmentStartNodeId())
                .pickupRoadSegmentEndNodeId(requestInfo.getPickupRoadSegmentEndNodeId())
                .dropOffRoadSegmentStartNodeId(requestInfo.getDropOffRoadSegmentStartNodeId())
                .dropOffRoadSegmentEndNodeId(requestInfo.getDropOffRoadSegmentEndNodeId())
                .detourConstraint(requestInfo.getDetourConstraint())
                .maxPickupDelaySeconds(requestInfo.getMaxPickupDelaySeconds())
                .load(requestInfo.getLoad())
                .requestedAt(requestInfo.getRequestedAt())
                .build();
    }

    private void sendRequestToOrpInput(ServiceRequest requestInfo) {
        kafkaTemplate.send(new ProducerRecord<>(
                "orp.input",
                null,
                requestInfo.getRequestId(),
                mapRequestInfoToOrpInputMessage(requestInfo),
                List.of(new RecordHeader("__TypeId__", ServiceRequestMessage.class.getName().getBytes(StandardCharsets.UTF_8)))
        ));
    }

    @Override public ServiceRequest dispatchRequest(ServiceRequest requestInfo) {
        requestInfo = requestInfoStorage.storeRequest(requestInfo);
        sendRequestToOrpInput(requestInfo);
        return requestInfo;
    }
}
