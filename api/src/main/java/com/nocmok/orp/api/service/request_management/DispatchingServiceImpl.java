package com.nocmok.orp.api.service.request_management;

import com.nocmok.orp.api.storage.request_management.RequestInfoStorage;
import com.nocmok.orp.api.storage.request_management.dto.RequestInfo;
import com.nocmok.orp.kafka.orp_input.ServiceRequestMessage;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.header.internals.RecordHeader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.List;

@Service
public class DispatchingServiceImpl implements DispatchingService {

    private RequestInfoStorage requestInfoStorage;
    private KafkaTemplate<String, Object> kafkaTemplate;

    @Autowired
    public DispatchingServiceImpl(RequestInfoStorage requestInfoStorage,
                                  KafkaTemplate<String, Object> kafkaTemplate) {
        this.requestInfoStorage = requestInfoStorage;
        this.kafkaTemplate = kafkaTemplate;
    }

    private ServiceRequestMessage mapRequestInfoToOrpInputMessage(RequestInfo requestInfo) {
        return ServiceRequestMessage.builder()
                .requestId(requestInfo.getRequestId())
                .recordedOriginLatitude(requestInfo.getRecordedOrigin().getLatitude())
                .recordedOriginLongitude(requestInfo.getRecordedOrigin().getLongitude())
                .recordedDestinationLatitude(requestInfo.getRecordedDestination().getLatitude())
                .recordedDestinationLongitude(requestInfo.getRecordedDestination().getLongitude())
                .pickupRoadSegmentStartNodeId(requestInfo.getPickupRoadSegment().getSourceId())
                .pickupRoadSegmentEndNodeId(requestInfo.getPickupRoadSegment().getTargetId())
                .dropOffRoadSegmentStartNodeId(requestInfo.getDropoffRoadSegment().getSourceId())
                .dropOffRoadSegmentEndNodeId(requestInfo.getDropoffRoadSegment().getTargetId())
                .detourConstraint(requestInfo.getDetourConstraint())
                .maxPickupDelaySeconds(requestInfo.getMaxPickupDelaySeconds())
                .load(requestInfo.getLoad())
                .requestedAt(requestInfo.getRequestedAt())
                .build();
    }

    private void sendRequestToOrpInput(RequestInfo requestInfo) {
        kafkaTemplate.send(new ProducerRecord<>(
                "orp.input",
                null,
                requestInfo.getRequestId(),
                mapRequestInfoToOrpInputMessage(requestInfo),
                List.of(new RecordHeader("__TypeId__", ServiceRequestMessage.class.getName().getBytes(StandardCharsets.UTF_8)))
        ));
    }

    @Override public RequestInfo dispatchRequest(RequestInfo requestInfo) {
        requestInfo = requestInfoStorage.storeRequest(requestInfo);
        sendRequestToOrpInput(requestInfo);
        return requestInfo;
    }
}
